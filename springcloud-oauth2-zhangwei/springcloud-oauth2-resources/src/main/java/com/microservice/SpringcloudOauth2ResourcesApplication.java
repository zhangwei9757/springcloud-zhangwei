package com.microservice;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.LocalDate;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
@Slf4j
public class SpringcloudOauth2ResourcesApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringcloudOauth2ResourcesApplication.class);
        ConfigurableApplicationContext applicationContext = application.run(args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String port = environment.getProperty("server.port");
        log.info("\n\r >>>>>> OAuth2 Resources Listener port: {}, day:{}", port, getNowDay());
    }

    public static int getNowDay() {
        LocalDate now = LocalDate.now();
        return now.getYear() * 10000 + now.getMonthValue() * 100 + now.getDayOfMonth();
    }
}
