package com.microservice.listener;

import com.microservice.bean.SchedulerTaskMessage;
import com.microservice.enums.ProtocolStausEnum;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.server.ExecutorGroupServerHandler;
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

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String schedulerMessage = new String(message.getBody());
        boolean notBlank = StringUtils.isNotBlank(schedulerMessage);

        if (notBlank) {
            SchedulerTaskMessage task = JsonUtils.fromJson(schedulerMessage, SchedulerTaskMessage.class);

            if (Objects.nonNull(task)) {
                String actuatorKey = task.getActuatorKey();

                // 如果执行器在自己名下就，发起执行
                Channel executeChannel = ExecutorGroupServerHandler.findChannel(actuatorKey);
                if (Objects.nonNull(executeChannel)) {

                    String json = JsonUtils.toJson(task);
                    MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol.newBuilder()
                            .setContent(json)
                            .setProtocol(ProtocolStausEnum.actuatorRequest.name())
                            .setLen(json.getBytes(PingPongUtils.CHARSET).length)
                            .build();
                    // 通知客户干活
                    executeChannel.writeAndFlush(messageProtocol);
                }
            }
        }
    }
}