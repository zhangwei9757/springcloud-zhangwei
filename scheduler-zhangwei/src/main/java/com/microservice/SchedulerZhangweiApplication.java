package com.microservice;

import com.microservice.scheduler.ExecutorGroupServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhangwei
 * @date 2020-10-03
 * <p>
 */
@SpringBootApplication
@Slf4j
public class SchedulerZhangweiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(SchedulerZhangweiApplication.class, args);
        try {
            ExecutorGroupServer groupServer = app.getBeanFactory().getBean(ExecutorGroupServer.class);
            groupServer.run();
        } catch (Exception e) {
            log.error(">>> netty服务器自启动失败, 原因: {}", e.getLocalizedMessage(), e);
        }
    }
}
