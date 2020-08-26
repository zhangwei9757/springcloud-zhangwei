package com.zhangwei.config;

import com.zhangwei.bean.WebsocketEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p>
 */
@Configuration
@EnableConfigurationProperties(WebsocketEndpointProperties.class)
public class WebsocketAutoConfiguration {
}
