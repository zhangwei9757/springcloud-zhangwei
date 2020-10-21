package com.microservice.listener;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SyncNotifyMessage;
import com.microservice.redis.ReceiveSyncNotifyMessage;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.server.ExecutorGroupServer;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p>集群通信, 如： a客户端发送消息到A服务器，集群其它服务器也广播消息到自己名下所有客户端
 * server   A        |       B      |      C
 * client   a,b,c        d,e,f          g,h,i,j
 *
 * <p> 集群通信流程
 * 1. 当 a 发送消息到 A 服务器， 集群其它服务器 B,C 自动同步消息
 * 2. 当 服务器 B,C 收到消息后， 广播至连接到各自的所有客户端
 * 3. d,e,f   g,h,i,j   自动同步消息接收到 a 客户端发送的消息
 */
@Slf4j
public class RedisSyncNotifyMessageListener implements MessageListener {

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

        String otherClientMessage = new String(message.getBody());

        boolean notBlank = StringUtils.isNotBlank(otherClientMessage);

        if (notBlank) {
            RedisMessage leader = defaultClientHandler.findLeader();
            groupServer.setLeader(leader);
            RedisMessage currentServer = groupServer.getCurrentServer();

            SyncNotifyMessage notifyMessage = JsonUtils.fromJson(otherClientMessage, SyncNotifyMessage.class);

            // 如果通知集群转发的成员，不是当前服务器通知同步消息处理器处理
            assert notifyMessage != null;
            if (!Objects.deepEquals(currentServer.getVoteKey(), notifyMessage.getClusterMemberVoteKey())) {
                Collection beansOfType = ApplicationContextUtil.getBeansOfType(ReceiveSyncNotifyMessage.class);
                if (!CollectionUtils.isEmpty(beansOfType)) {
                    beansOfType.parallelStream().forEach(f -> ((ReceiveSyncNotifyMessage) f).onMessage(notifyMessage));
                }
                if (properties.isAllowLog()) {
                    RedisMessage source = defaultClientHandler.findByKey(notifyMessage.getClusterMemberVoteKey(), RedisMessage.class);
                    log.info(">>> 集群成员: {} => {}, 收到其它成员: {} => {}, 消息: {}",
                            currentServer.ipMappingPort(), currentServer.getCurrentRaftStatus(),
                            source.ipMappingPort(), source.getCurrentRaftStatus(), notifyMessage);
                }
            }

        }
    }
}