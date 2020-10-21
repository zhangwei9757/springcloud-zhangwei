package com.microservice;

import com.microservice.annotation.EnableExecutorClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableExecutorClient
public class SchedulerExampleExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerExampleExecutorApplication.class, args);
    }

}
