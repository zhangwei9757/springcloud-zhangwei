package com.microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
public class ProtocolBuffersConfig {

    /**
     * protobuf 序列化
     */
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    /**
     * protobuf 反序列化
     */
    @Bean(value = "protobufRestTemplate")
    RestTemplate restTemplate(ProtobufHttpMessageConverter protobufHttpMessageConverter) {
        return new RestTemplate(Collections.singletonList(protobufHttpMessageConverter));
    }
}
