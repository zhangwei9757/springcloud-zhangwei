package com.microservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @author zw
 * @date 2021-03-31
 * <p>
 */
@Component
@Slf4j
public class ActiveMqListener {

    /**
     * 使用JmsListener配置消费者监听的队列，其中message是接收到的消息
     *
     * @param message
     * @return
     */
    @JmsListener(destination = "ActiveMQ-Queue-ZhangWei")
    @SendTo("ActiveMQ-Out-Queue-ZhangWei")
    public String handleMessage(String message) {
        log.info(">>>>>>>> 队列[ActiveMQ-Queue-ZhangWei] 接收到消息: {}", message);
        return "成功接受消息: " + message;
    }
}
