package com.microservice.server;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.proto.MessageProtocolPoJo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 * 粘包，拆包旧解决方案：
 * pipeline.addLast("decoder", new WebMessageDecoder());
 * pipeline.addLast("encoder", new WebMessageEncoder());
 */
@Slf4j
@Component
public class ExecutorGroupServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private ExecutorGroupServerHandler executorGroupServerHandler;

    @Resource
    private SchedulerConfigurationProperties properties;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(properties.getReaderIdleTimeNanos(),
                properties.getWriterIdleTimeNanos(),
                properties.getAllIdleTimeNanos(),
                TimeUnit.SECONDS));
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufDecoder(MessageProtocolPoJo.MessageProtocol.getDefaultInstance()));
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(executorGroupServerHandler);

        log.info("----------------------------------------ExecutorGroupServerChannelInitializer----------------------------------------");
        log.info("addLast => {}, readerIdleTime: {}, writerIdleTime: {}, allIdleTime: {}",
                IdleStateHandler.class.getSimpleName(), properties.getReaderIdleTimeNanos(),
                properties.getWriterIdleTimeNanos(), properties.getAllIdleTimeNanos());
        log.info("addLast => {}", ProtobufVarint32FrameDecoder.class.getSimpleName());
        log.info("addLast => {}", ProtobufVarint32LengthFieldPrepender.class.getSimpleName());
        log.info("addLast => {}", ProtobufDecoder.class.getSimpleName());
        log.info("addLast => {}", MessageProtocolPoJo.MessageProtocol.class.getSimpleName());
        log.info("addLast => {}", ProtobufEncoder.class.getSimpleName());
        log.info("addLast => {}", executorGroupServerHandler.getClass().getSimpleName());
        log.info("----------------------------------------ExecutorGroupServerChannelInitializer----------------------------------------");
    }
}
