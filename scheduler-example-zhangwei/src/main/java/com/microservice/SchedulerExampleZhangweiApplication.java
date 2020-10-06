package com.microservice;

import com.microservice.annotation.EnableExecutorClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableExecutorClient
public class SchedulerExampleZhangweiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerExampleZhangweiApplication.class, args);
    }

}
