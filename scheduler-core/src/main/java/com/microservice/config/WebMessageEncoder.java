package com.microservice.config;

import com.microservice.proto.MessageProtocolPoJo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 */
public class WebMessageEncoder extends MessageToByteEncoder<MessageProtocolPoJo.MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocolPoJo.MessageProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent().getBytes(Charset.forName("utf-8")));
    }
}
