package com.microservice.listener;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerTask;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.scheduler.ExecutorGroupServer;
import com.microservice.scheduler.ExecutorGroupServerHandler;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.PingPongUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p> 集群调度受理通知
 */
@Slf4j
public class RedisSchedulerNotifyListener implements MessageListener {

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


        String schedulerMessage = new String(message.getBody());
        boolean notBlank = StringUtils.isNotBlank(schedulerMessage);

        if (notBlank) {
            SchedulerTask task = JsonUtils.fromJson(schedulerMessage, SchedulerTask.class);

            if (Objects.nonNull(task)) {
                String actuatorKey = task.getActuatorKey();
                Channel channel = ExecutorGroupServerHandler.findChannel(actuatorKey);
                assert channel != null;
                String json = JsonUtils.toJson(task);
                MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol.newBuilder()
                        .setContent(json)
                        .setLen(json.getBytes(PingPongUtils.CHARSET).length)
                        .build();
                // 通知客户干活
                channel.writeAndFlush(messageProtocol);
            }
        }
    }
}