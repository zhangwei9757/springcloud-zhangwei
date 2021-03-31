package com.microservice.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

/**
 * @author zw
 * @date 2021-03-31
 * <p>
 */
@Configuration
public class ActiveMqConfig {

    /**
     * 定义存放消息的队列
     *
     * @return 队列
     */
    @Bean
    public Queue queue() {
        return new ActiveMQQueue("ActiveMQ-Queue-ZhangWei");
    }
}
