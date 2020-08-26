package com.zhangwei.config;

import com.zhangwei.annotation.EnableWebSocketEndpoint;
import com.zhangwei.bean.WebsocketEndpointProperties;
import com.zhangwei.websocket.WebSocketHandler;
import com.zhangwei.websocket.WebSocketHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.annotation.Resource;

/**
 * @author zhangwei
 * @date 2020-08-16
 * <p> websocket 配置
 */
@Slf4j
@Configuration
@ConditionalOnBean(annotation = EnableWebSocketEndpoint.class)
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Resource
    private WebsocketEndpointProperties websocketEndpointProperties;

    private final Object monitor = new Object();

    private void createWebsocketEndpointProperties() {
        synchronized (monitor) {
            if (null == websocketEndpointProperties) {
                websocketEndpointProperties = new WebsocketEndpointProperties();
                log.info(">>> WebsocketEndpointProperties Successfully initialized: {}", websocketEndpointProperties);
            }
        }
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        this.createWebsocketEndpointProperties();
        webSocketHandlerRegistry.addHandler(webSocketHandler(),
                websocketEndpointProperties.getWebsocketEndpoint())
                .addInterceptors(webSocketHandshakeInterceptor())
                .setAllowedOrigins(websocketEndpointProperties.getWebsocketEndpointAllowedOrigins());
        webSocketHandlerRegistry.addHandler(webSocketHandler(),
                websocketEndpointProperties.getWebsocketJsEndpoint())
                .addInterceptors(webSocketHandshakeInterceptor())
                .setAllowedOrigins(websocketEndpointProperties.getWebsocketJsEndpointAllowedOrigins())
                .withSockJS();
        log.info(">>> WebSocketConfiguration Successfully register registerWebSocketHandlers...");
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }

    @Bean
    public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor() {
        return new WebSocketHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        this.createWebsocketEndpointProperties();

        log.info(">>> 设置 ServletServerContainerFactoryBean is servlet server container...");
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();

        // 3000L
        if (websocketEndpointProperties.getAsyncSendTimeout() > 0) {
            container.setAsyncSendTimeout(websocketEndpointProperties.getAsyncSendTimeout());
        }

        // 1800000L
        if (websocketEndpointProperties.getMaxSessionIdleTimeout() > 0) {
            container.setMaxSessionIdleTimeout(websocketEndpointProperties.getMaxSessionIdleTimeout());
        }

        // 1024 * 1024
        if (websocketEndpointProperties.getMaxTextMessageBufferSize() > 0) {
            container.setMaxTextMessageBufferSize(websocketEndpointProperties.getMaxTextMessageBufferSize());
        }

        // 1024 * 1024 * 20
        if (websocketEndpointProperties.getMaxBinaryMessageBufferSize() > 0) {
            container.setMaxBinaryMessageBufferSize(websocketEndpointProperties.getMaxBinaryMessageBufferSize());
        }
        return container;
    }
}
