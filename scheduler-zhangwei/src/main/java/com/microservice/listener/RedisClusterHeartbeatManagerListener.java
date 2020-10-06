package com.microservice.listener;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerServerHeartbeat;
import com.microservice.enums.HostStatusEnum;
import com.microservice.enums.RaftStatusEnum;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.scheduler.ExecutorGroupServer;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p> 集群心跳监管处理
 */
@Slf4j
public class RedisClusterHeartbeatManagerListener implements MessageListener {

    private RedisDefaultClientHandler defaultClientHandler;
    private RedisDefaultGroupServerHandler defaultGroupServerHandler;
    private SchedulerConfigurationProperties properties;
    private ExecutorGroupServer groupServer;

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
                log.error(">>> 服务器: {}, 管理集群成员心跳失败, 未发现 Leader 信息...", currentServer.ipMappingPort());
            }
            currentServer.setCurrentRaftStatus(RaftStatusEnum.FOLLOWER);
            defaultClientHandler.updateMessageByKey(currentServer.getVoteKey(), JsonUtils.toJson(currentServer));
            defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
            return;
        }

        if (!Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort())) {
            return;
        }

        // 只有集群 Leader 才可以受理集群成员心跳事件, Leader 开始集群监管
        String memberMessage = new String(message.getBody());
        RedisMessage messageEvent = JsonUtils.fromJson(memberMessage, RedisMessage.class);

        if (Objects.isNull(messageEvent)) {
            if (properties.isAllowLog()) {
                log.error(">>> 服务器: {}, 管理心跳失败, 订阅消息丢失...", currentServer.ipMappingPort());
            }
            return;
        }

        this.scanning(messageEvent, leader);
    }


    /**
     * 进入 Leader 心跳监管时间
     *
     * @param message 集群成员心跳消息
     */
    public void scanning(RedisMessage message, RedisMessage leader) {
        // 心跳监管处理集群成员心跳事件
        SchedulerServerHeartbeat heartbeat = message.getHeartbeat();
        long heartbeatTimeout = properties.getClusterHeartbeatTimeout();
        long nowSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
        LocalDateTime lastHeartbeatTime = heartbeat.getLastHeartbeatTime();
        long lastHeartbeatSecond = lastHeartbeatTime.toEpochSecond(ZoneOffset.ofHours(8));

        // 最后一次发送的心跳时间 + 心跳超时时间 > 当前时间, 表示已超时
        boolean status = nowSecond - lastHeartbeatSecond <= heartbeatTimeout;
        // false: 标记下线, true: 标记上线
        if (properties.isAllowLog()) {
            log.info(">>> {} => {}, 受理集群成员心跳事件: {} => {}",
                    leader.ipMappingPort(), leader.getCurrentRaftStatus(),
                    message.ipMappingPort(), status ? HostStatusEnum.UP : HostStatusEnum.DOWN);
        }
        defaultClientHandler.onLineOrOffLine(message, status);
    }
}