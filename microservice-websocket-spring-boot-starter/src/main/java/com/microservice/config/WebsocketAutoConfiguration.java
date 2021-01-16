package com.microservice.config;

import com.microservice.bean.WebsocketEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p>
 */
@Configuration
@EnableConfigurationProperties(WebsocketEndpointProperties.class)
@ComponentScan(value = "com.microservice")
public class WebsocketAutoConfiguration {
}
