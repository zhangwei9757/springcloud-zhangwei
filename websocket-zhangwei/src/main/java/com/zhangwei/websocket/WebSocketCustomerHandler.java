package com.zhangwei.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.nio.ByteBuffer;

/**
 * @author zhangwei
 * @date 2020-08-16
 * <p> websocket 消息处理器
 */
@Slf4j
public class WebSocketCustomerHandler extends TextWebSocketHandler {

    @Resource
    private WebSocketServer server;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(">>> incoming connection thread(" + Thread.currentThread().getName() + ":::" + Thread.currentThread().getId());
        server.onAddSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("****** session:" + session.getId() + " 链接关闭,状态:" + status.toString());
        server.onDelSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();

            int pos = payload.indexOf("|");
            if (pos == -1 || pos == payload.length() - 1) {
                return;
            }

            String type = payload.substring(0, pos);
            String body = payload.substring(pos + 1);

            server.onMessage(session, type, body);
        } catch (Exception ex) {
            log.error("handleTextMessage error:", ex);
            server.onError(session, ex.getMessage());
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            ByteBuffer buffer = message.getPayload();
            server.onMessage(session, buffer);
        } catch (Exception e) {
            log.error("handleBinaryMessage error:", e);
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("handlerPongMessage");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("handleTransportError:" + exception.getMessage());
        server.onError(session, exception.getMessage());
    }
}
