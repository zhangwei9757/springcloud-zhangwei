package com.microservice;

import com.microservice.annotation.EnableProtobufConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProtobufConverter
public class MicroserviceProtobufSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProtobufSpringBootStarterApplication.class, args);
    }

}
