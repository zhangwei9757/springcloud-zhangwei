package com.microservice.protocol;

import com.microservice.proto.MessageProtocolPoJo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 * 粘包，拆包旧解决方案：
 * pipeline.addLast("encoder", new WebMessageEncoder());
 * pipeline.addLast("decoder", new WebMessageDecoder());
 */
@Slf4j
public class ExecutorClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ExecutorClient executorClient;

    public ExecutorClientChannelInitializer() {
    }

    public ExecutorClientChannelInitializer(ExecutorClient executorClient) {
        this.executorClient = executorClient;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ReconnectHandler(executorClient));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ProtobufDecoder(MessageProtocolPoJo.MessageProtocol.getDefaultInstance()));
        pipeline.addLast(new ExecutorClientHandler());

        log.info("----------------------------------------ExecutorClientChannelInitializer----------------------------------------");
        log.info("addLast => {}", ReconnectHandler.class.getSimpleName());
        log.info("addLast => {}", ProtobufVarint32LengthFieldPrepender.class.getSimpleName());
        log.info("addLast => {}", ProtobufVarint32FrameDecoder.class.getSimpleName());
        log.info("addLast => {}", ProtobufEncoder.class.getSimpleName());
        log.info("addLast => {}", ProtobufDecoder.class.getSimpleName());
        log.info("addLast => {}", MessageProtocolPoJo.MessageProtocol.class.getSimpleName());
        log.info("----------------------------------------ExecutorClientChannelInitializer----------------------------------------");
    }
}
