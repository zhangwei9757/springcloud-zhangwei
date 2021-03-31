package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PackageServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackageServerApplication.class, args);
	}

}
