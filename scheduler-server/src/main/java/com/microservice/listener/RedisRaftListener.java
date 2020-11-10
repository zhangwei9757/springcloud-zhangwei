package com.microservice.listener;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.enums.RaftStatusEnum;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.server.ExecutorGroupServer;
import com.microservice.server.SchedulerActuatorScanningHandler;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RandomUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p>
 * redis 订阅处理器
 * 1. 处理集群raft 算法
 * 2. 处理集群leader心跳
 * 3. 处理集群数据同步
 * 4. 处理业务数据
 */
@Slf4j
public class RedisRaftListener implements MessageListener {

    private RedisDefaultClientHandler defaultClientHandler;
    private RedisDefaultGroupServerHandler defaultGroupServerHandler;
    private SchedulerConfigurationProperties properties;
    private ExecutorGroupServer groupServer;
    private SchedulerActuatorScanningHandler actuatorScanningHandler;
    private ScheduledFuture<?> heartbeatSchedule;
    private boolean directConnectLeader = true;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        defaultGroupServerHandler = ApplicationContextUtil.getBean(RedisDefaultGroupServerHandler.class);
        properties = ApplicationContextUtil.getBean(SchedulerConfigurationProperties.class);
        groupServer = ApplicationContextUtil.getBean(ExecutorGroupServer.class);
        actuatorScanningHandler = ApplicationContextUtil.getBean(SchedulerActuatorScanningHandler.class);

        String memberMessage = new String(message.getBody());
        RedisMessage member = JsonUtils.fromJson(memberMessage, RedisMessage.class);

        RedisMessage currentServer = groupServer.getCurrentServer();
        // 当集群成员收到上线通知， 此时开始选举
        if (Objects.isNull(member)) {
            if (properties.isAllowLog()) {
                log.error(">>> 服务器: {}, 无法开始选举, 订阅消息丢失...", currentServer.ipMappingPort());
            }
            return;
        }

        if (Objects.nonNull(heartbeatSchedule)) {
            heartbeatSchedule.cancel(true);
        }
        raftElectionHandler();
    }

    /**
     * 进入选举时间, 先出 Leader 直接退出
     */
    private void raftElectionHandler() {
        RedisMessage currentServer = groupServer.getCurrentServer();
        Channel channel = groupServer.getChannel();

        // 开始自动选举
        heartbeatSchedule = channel.eventLoop().schedule(() -> {
            LocalDateTime now = LocalDateTime.now();
            long nowSecond = now.toEpochSecond(ZoneOffset.ofHours(8));

            // 1. 先获取Leader
            String leaderMessage = defaultClientHandler.findLeaderMessage();
            RedisMessage leader = StringUtils.isBlank(leaderMessage) ?
                    null :
                    JsonUtils.fromJson(leaderMessage, RedisMessage.class);

            // 2.1 如果获取到，表示已选举出leader ,保持集群心跳即可
            if (Objects.nonNull(leader)) {
                boolean leaderWebStatus = actuatorScanningHandler.leaderWebStatus();
                if (!leaderWebStatus) {
                    defaultGroupServerHandler.leaderOffLine();
                }

                groupServer.setLeader(leader);
                boolean deepEquals = Objects.deepEquals(leader.ipMappingPort(), currentServer.ipMappingPort());
                currentServer.setCurrentRaftStatus(deepEquals ? RaftStatusEnum.LEADER : RaftStatusEnum.FOLLOWER);
                defaultClientHandler.updateMessageByKey(currentServer.getVoteKey(), JsonUtils.toJson(currentServer));

                if (properties.isAllowLog()) {
                    log.info(">>> 当前服务器: {} => {}, 集群发现 Leader: {}",
                            currentServer.ipMappingPort(),
                            currentServer.getCurrentRaftStatus(),
                            leader.ipMappingPort());
                }
            } else {
                directConnectLeader = false;
            }

            // 2.2 如果未获取到, 且超出选举时间，转换状态，准备选举 从 follower 切换到 candidate
            if (groupServer.getLastVoteTime() <= 0) {
                groupServer.setLastVoteTime(nowSecond);
            }
            long diff = nowSecond - groupServer.getLastVoteTime();

            if (Objects.isNull(leader) && diff >= properties.getClusterVoteTimeout()) {
                RaftStatusEnum tempStatus = currentServer.getCurrentRaftStatus();
                currentServer.setCurrentRaftStatus(RaftStatusEnum.CANDIDATE);

                defaultClientHandler.updateMessageByKey(currentServer.getVoteKey(), JsonUtils.toJson(currentServer));

                if (properties.isAllowLog()) {
                    log.info(">>> 当前服务器: {} => {}, 集群未发现 Leader, 选举超时时间: {}s, 准备开始选举, 修改本服务器状态: {} => {}",
                            currentServer.ipMappingPort(),
                            tempStatus,
                            properties.getClusterVoteTimeout(),
                            tempStatus,
                            currentServer.getCurrentRaftStatus());
                }
            }

            // 3. 如果状态为 candidate, 表示发起选举
            if (RaftStatusEnum.CANDIDATE == currentServer.getCurrentRaftStatus()) {
                Set<String> serverAddress = properties.findServerAddress();
                String server = RandomUtils.getInList(serverAddress);

                currentServer.setVoteTime(nowSecond);
                currentServer.setVote(server);

                defaultClientHandler.updateMessageByKey(currentServer.getVoteKey(), JsonUtils.toJson(currentServer));

                if (properties.isAllowLog()) {
                    log.info(">>> 当前服务器: {} => {}, 开始投票选举...",
                            currentServer.ipMappingPort(), currentServer.getCurrentRaftStatus());
                }

                // 标记选举leader时间
                groupServer.setLastVoteTime(nowSecond);
                try {
                    /*
                      ========================
                      ===== Raft 选举事件 =====
                      ========================
                     */
                    defaultGroupServerHandler.vote(currentServer);
                } catch (Exception ignored) {
                } finally {
                    defaultGroupServerHandler.flushClusterList(currentServer, false);
                }
            }

            // -1集群成员, 再次获取Leader，防止重复加载
            leaderMessage = defaultClientHandler.findLeaderMessage();
            leader = StringUtils.isBlank(leaderMessage) ?
                    null :
                    JsonUtils.fromJson(leaderMessage, RedisMessage.class);

            if (Objects.isNull(leader)) {
                // 进入选举时间
                this.raftElectionHandler();
            } else {
                groupServer.setLeader(leader);
                // 如果是自动连接就直接，进入心跳阶段
                if (directConnectLeader) {
                    // 进入集群成员自我心跳时间
                    defaultGroupServerHandler.clusterFlushStatus(currentServer);
                }
            }
        }, properties.getClusterVoteTime(), TimeUnit.SECONDS);
    }
}