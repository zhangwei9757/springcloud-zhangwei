package com.zhangwei.websocket;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.ByteBuffer;

/**
 * @author zhangwei
 * @date 2020-08-16
 * <p> 子协议
 */
public class BaseProtocol {

    public BaseProtocol() {
    }

    /**
     * 预处理
     *
     * @param webSocketUser
     */
    protected void preProcess(WebSocketUser webSocketUser, ByteBuffer buffer) {
    }

    /**
     * 后处理
     *
     * @param webSocketUser
     */
    protected void postProcess(WebSocketUser webSocketUser, ByteBuffer buffer) {
    }

    /**
     * 协议接收后的处理逻辑
     */
    public void process(WebSocketUser webSocketUser, ByteBuffer buffer) {
        preProcess(webSocketUser, buffer);
        onProcess(webSocketUser, buffer);
        postProcess(webSocketUser, buffer);
    }

    public void onProcess(WebSocketUser webSocketUser, ByteBuffer buffer) {
    }

    @JsonIgnore
    public String getProtoType() {
        return getClass().getSimpleName();
    }

    @JsonIgnore
    public int getBinaryProtocol() {
        return -1;
    }
}
