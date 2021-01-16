package com.microservice.config;

import com.microservice.bean.PyCallbackProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p>
 */
@Configuration
@EnableConfigurationProperties({PyCallbackProperties.class})
@ComponentScan(value = "com.microservice")
public class PyCallbackAutoConfiguration {
}
