package com.zhangwei;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;

@SpringBootApplication
@MapperScan("com.zhangwei.mapper")
@Slf4j
@EnableAsync
@EnableTransactionManagement
public class SpringcloudElasticsearch780ZhangweiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringcloudElasticsearch780ZhangweiApplication.class);
        ConfigurableApplicationContext applicationContext = application.run(args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String port = environment.getProperty("server.port");
        log.info("\n\r >>>>>> Elasticsearch Listener port: {}, day:{}", port, getNowDay());
    }

    public static int getNowDay() {
        LocalDate now = LocalDate.now();
        return now.getYear() * 10000 + now.getMonthValue() * 100 + now.getDayOfMonth();
    }
}
