//package com.zhangwei.config;
//
//import com.zhangwei.websocket.WebSocketCustomerHandler;
//import com.zhangwei.websocket.WebSocketHandshakeInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
//
///**
// * @author zhangwei
// * @date 2020-08-16
// * <p> websocket 配置
// */
//@Configuration
//@EnableWebSocket
//@Slf4j
//public class WebSocketConfiguration implements WebSocketConfigurer {
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
//        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/socket")
//                .addInterceptors(webSocketHandshakeInterceptor())
//                .setAllowedOrigins("*");
//        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/socketJs")
//                .addInterceptors(webSocketHandshakeInterceptor())
//                .setAllowedOrigins("*")
//                .withSockJS();
//    }
//
//    @Bean
//    public WebSocketCustomerHandler webSocketHandler() {
//        return new WebSocketCustomerHandler();
//    }
//
//    @Bean
//    public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor() {
//        return new WebSocketHandshakeInterceptor();
//    }
//
//    @Bean
//    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
//        log.info("----------- 设置 servlet server container...");
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//
//        // container.setAsyncSendTimeout(3000L);
//        // container.setMaxSessionIdleTimeout(1800000L);
//        container.setMaxTextMessageBufferSize(1024 * 1024);
//        container.setMaxBinaryMessageBufferSize(1024 * 1024 * 20);
//        return container;
//    }
//}
