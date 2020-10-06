package com.microservice.config;

import com.microservice.proto.MessageProtocolPoJo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 */
public class WebMessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 1. binary -> MessageProtocol data package
        int len = in.readInt();
        byte[] content = new byte[len];
        in.readBytes(content);

        // 2. MessageProtocol -> handle
        MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol
                .newBuilder()
                .setLen(len)
                .setContent(new String(content, Charset.forName("utf-8")))
                .build();

        out.add(messageProtocol);
    }
}
