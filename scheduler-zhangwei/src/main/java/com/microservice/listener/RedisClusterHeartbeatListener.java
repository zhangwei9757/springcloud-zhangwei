package com.microservice.listener;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerServerHeartbeat;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.scheduler.ExecutorGroupServer;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p>集群服务器心跳
 */
@Slf4j
public class RedisClusterHeartbeatListener implements MessageListener {

    private RedisDefaultClientHandler defaultClientHandler;
    private RedisDefaultGroupServerHandler defaultGroupServerHandler;
    private SchedulerConfigurationProperties properties;
    private ExecutorGroupServer groupServer;

    private static ScheduledFuture<?> heartbeatSchedule;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        defaultGroupServerHandler = ApplicationContextUtil.getBean(RedisDefaultGroupServerHandler.class);
        properties = ApplicationContextUtil.getBean(SchedulerConfigurationProperties.class);
        groupServer = ApplicationContextUtil.getBean(ExecutorGroupServer.class);

        assert groupServer != null;
        RedisMessage currentServer = groupServer.getCurrentServer();
        RedisMessage leader = defaultClientHandler.findLeader();
        groupServer.setLeader(leader);

        if (Objects.isNull(leader)) {
            if (properties.isAllowLog()) {
                log.error(">>> 服务器: {}, 向 Leader 心跳失败, 未发现 Leader 信息...", currentServer.ipMappingPort());
            }
            defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
            if (Objects.nonNull(heartbeatSchedule)) {
                heartbeatSchedule.cancel(true);
            }
            return;
        }

        // 只有非 Leader 集群成员, 才可以进入心跳时间
        if (!Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort())) {
            // 集群成员进入心跳时间, 上次心跳自动关闭
            if (Objects.nonNull(heartbeatSchedule)) {
                heartbeatSchedule.cancel(true);
            }
            this.heartbeat();
        }
    }


    /**
     * 进入心跳接管时间
     */
    public void heartbeat() {
        RedisMessage currentServer = groupServer.getCurrentServer();
        Channel channel = groupServer.getChannel();

        heartbeatSchedule = channel.eventLoop().schedule(() -> {
            RedisMessage leader = defaultClientHandler.findLeader();
            if (properties.isAllowLog()) {
                log.info("");
                log.info(">>> ----------------------------heartbeat start-------------------------------");
            }

            if (Objects.isNull(leader)) {
                if (properties.isAllowLog()) {
                    log.error(">>> 服务器: {}, 向 Leader 心跳失败, 未发现 Leader 信息...", currentServer.ipMappingPort());
                }
                defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
                return;
            } else {
                // 1. 无论自己注册信息是否存在，必须保存自己的在线信息一致性
                currentServer.setEnableHealthCheck(true);
                defaultGroupServerHandler.flushClusterRegister(currentServer);

                // 2. 集群成员，必须心跳检查 leader, 如果下线, 马上清除 leader
                boolean isLeader = Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort());
                boolean leaderHealthCheck = defaultGroupServerHandler.clusterHealthCheck(currentServer, isLeader, leader);
                if (!leaderHealthCheck) {
                    // 表示leader 掉线
                    defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
                    return;
                }

                // 3. 自己的心跳必须发送给集群 Leader
                SchedulerServerHeartbeat heartbeat = new SchedulerServerHeartbeat();
                heartbeat.setServerIp(currentServer.getVoteHost())
                        .setServerPort(currentServer.getRegisterPort())
                        .setLeaderHostAddress(leader.getVoteHost())
                        .setLeaderHostPort(leader.getRegisterPort())
                        .setLastHeartbeatTime(LocalDateTime.now());

                RedisMessage temp = new RedisMessage();
                BeanUtils.copyProperties(currentServer, temp);
                temp.setHeartbeat(heartbeat);

                defaultClientHandler.updateMessageByKey(currentServer.getHeartbeatKey(), JsonUtils.toJson(heartbeat));
                defaultClientHandler.eventPush(RedisListenerEnum.CLUSTER_HEARTBEAT_MANAGER, JsonUtils.toJson(temp));

                if (properties.isAllowLog()) {
                    log.info(">>>> 服务器: {} => {}, 推送心跳事件: {}",
                            currentServer.ipMappingPort(), currentServer.getCurrentRaftStatus(), heartbeat);
                }
            }

            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------heartbeat end-------------------------------");
            }

            // 自动向 Leader 心跳
            this.heartbeat();

        }, properties.getClusterHeartbeat(), TimeUnit.SECONDS);
    }
}