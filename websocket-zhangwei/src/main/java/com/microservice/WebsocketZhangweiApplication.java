package com.microservice;

import com.microservice.annotation.EnableWebSocketEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableWebSocketEndpoint
public class WebsocketZhangweiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketZhangweiApplication.class, args);
    }

}
