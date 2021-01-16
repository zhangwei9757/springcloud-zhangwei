package com.microservice.config;

import com.microservice.handler.retry.DefaultRetryPolicy;
import com.microservice.handler.retry.RetryPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-09-22
 * <p>
 */
@Configuration
public class RetryPolicyAutoConfig {

    @Bean
    @ConditionalOnMissingBean(RetryPolicy.class)
    public RetryPolicy retryPolicy() {
        return new DefaultRetryPolicy();
    }
}
