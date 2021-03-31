package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ActivemqConsumersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivemqConsumersApplication.class, args);
	}

}
