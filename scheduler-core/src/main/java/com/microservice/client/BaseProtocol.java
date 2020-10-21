package com.microservice.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.proto.MessageProtocolPoJo;
import io.netty.channel.Channel;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p> 自定义回调协议处理方案
 */
public class BaseProtocol{

    public BaseProtocol() {
    }

    /**
     * 预处理
     *
     * @param messageProtocol
     */
    protected void preProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
    }

    /**
     * 后处理
     *
     * @param messageProtocol
     */
    protected void postProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
    }

    /**
     * 协议接收后的处理逻辑
     */
    public void process(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        preProcess(messageProtocol, channel);
        onProcess(messageProtocol, channel);
        postProcess(messageProtocol, channel);
    }

    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
    }

    @JsonIgnore
    public String getProtoType() {
        return getClass().getSimpleName();
    }
}
