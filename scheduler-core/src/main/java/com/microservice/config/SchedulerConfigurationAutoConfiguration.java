package com.microservice.config;

import com.microservice.bean.SchedulerConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p>
 */
@Configuration
@EnableConfigurationProperties({SchedulerConfigurationProperties.class})
@ComponentScan(value = "com.microservice")
public class SchedulerConfigurationAutoConfiguration {
}
