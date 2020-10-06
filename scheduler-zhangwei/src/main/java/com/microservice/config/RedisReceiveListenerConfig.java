package com.microservice.config;

import com.microservice.enums.RedisListenerEnum;
import com.microservice.listener.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author zw
 * @date 2020-10-02
 * <p>
 */
@Configuration
public class RedisReceiveListenerConfig {

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(redisClusterLeaderHeartbeatManagerListener(), new PatternTopic(RedisListenerEnum.CLUSTER_HEARTBEAT_MANAGER.getType()));
        container.addMessageListener(redisClusterMemberHeartbeatListener(), new PatternTopic(RedisListenerEnum.CLUSTER_MEMBER_HEARTBEAT.getType()));
        container.addMessageListener(redisClusterRefreshListener(), new PatternTopic(RedisListenerEnum.CLUSTER_REFRESH.getType()));
        container.addMessageListener(redisRaftListener(), new PatternTopic(RedisListenerEnum.RAFT.getType()));
        container.addMessageListener(redisServerNotifyListener(), new PatternTopic(RedisListenerEnum.SERVER_NOTIFY.getType()));
        container.addMessageListener(redisSyncClientMessageListener(), new PatternTopic(RedisListenerEnum.SYNC_CLIENT_MESSAGE.getType()));
        return container;
    }

    @Bean
    public MessageListenerAdapter redisClusterLeaderHeartbeatManagerListener() {
        return new MessageListenerAdapter(new RedisClusterHeartbeatManagerListener(), "onMessage");
    }

    @Bean
    public MessageListenerAdapter redisClusterMemberHeartbeatListener() {
        return new MessageListenerAdapter(new RedisClusterHeartbeatListener(), "onMessage");
    }

    @Bean
    public MessageListenerAdapter redisClusterRefreshListener() {
        return new MessageListenerAdapter(new RedisClusterRefreshListener(), "onMessage");
    }

    @Bean
    public MessageListenerAdapter redisRaftListener() {
        return new MessageListenerAdapter(new RedisRaftListener(), "onMessage");
    }


    @Bean
    public MessageListenerAdapter redisServerNotifyListener() {
        return new MessageListenerAdapter(new RedisSchedulerNotifyListener(), "onMessage");
    }

    @Bean
    public MessageListenerAdapter redisSyncClientMessageListener() {
        return new MessageListenerAdapter(new RedisSyncNotifyMessageListener(), "onMessage");
    }
}
