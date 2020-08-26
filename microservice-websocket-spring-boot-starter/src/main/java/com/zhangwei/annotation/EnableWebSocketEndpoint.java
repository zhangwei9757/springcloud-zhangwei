package com.zhangwei.annotation;

import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p> Websocket 端点配置开关
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableWebSocket
public @interface EnableWebSocketEndpoint {
}
