package com.zhangwei.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2020-08-16
 * <p>
 */
@Slf4j
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 获得 accessToken
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            String accessToken = serverRequest.getServletRequest().getParameter("accessToken");

            if (Objects.isNull(accessToken)) {
                log.error("客户端提交的数据中没有accessToken, 无法查找用户信息...");
                return false;
            }

            Map<String, String[]> paramters = serverRequest.getServletRequest().getParameterMap();
            Map<String, String> httpParams = paramters
                    .entrySet().stream()
                    .filter(entry -> entry.getValue().length > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
            attributes.putAll(httpParams);
        }

        // 调用父方法，继续执行逻辑
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
