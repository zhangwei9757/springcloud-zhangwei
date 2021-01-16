package com.microservice;

import com.microservice.server.ExecutorGroupServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhangwei
 * @date 2020-10-03
 * <p>
 */
@SpringBootApplication
@Slf4j
@MapperScan(basePackages = "com.microservice.mapper")
@EnableTransactionManagement
@EnableCaching
public class SchedulerServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(SchedulerServerApplication.class, args);
        try {
            ExecutorGroupServer groupServer = app.getBeanFactory().getBean(ExecutorGroupServer.class);
            groupServer.run();
        } catch (Exception e) {
            log.error(">>> netty服务器自启动失败, 原因: {}", e.getLocalizedMessage(), e);
        }
    }
}
