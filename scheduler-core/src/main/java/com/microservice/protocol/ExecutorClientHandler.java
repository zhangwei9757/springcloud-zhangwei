package com.microservice.protocol;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerTask;
import com.microservice.handler.ExecutorJobManagerHandler;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.protocol.request.ActuatorRequest;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.PingPongUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 */
@Slf4j
public class ExecutorClientHandler extends SimpleChannelInboundHandler<MessageProtocolPoJo.MessageProtocol> {

    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelActive ------------------------");
        ExecutorJobManagerHandler executorJobHandler = ApplicationContextUtil.getBean(ExecutorJobManagerHandler.class);
        assert null != executorJobHandler;
        Channel channel = ctx.channel();
        try {
            SchedulerConfigurationProperties properties = ApplicationContextUtil.getBean(SchedulerConfigurationProperties.class);
            // 自动注册执行器
            executorJobHandler.registerExecutor(channel, properties);
        } finally {
            // 自动初始化 executorJob 注解任务
            executorJobHandler.initExecutorJobHandlerMethodRepository();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocolPoJo.MessageProtocol msg) throws Exception {
        log.info("------------------------ client channelRead0 ------------------------");

        String content = msg.getContent();

        Channel channel = ctx.channel();

        if (Objects.deepEquals(content, PingPongUtils.PING)) {
            // 1. 心跳检测，直接回复即可，无需其它处理
            pong(channel);
            return;
        }

        SchedulerTask task = JsonUtils.fromJson(content, SchedulerTask.class);
        if (Objects.nonNull(task)) {
            // 自动调用当前客户端的指定执行器方法
            ActuatorRequest actuatorRequest = ApplicationContextUtil.getBean(ActuatorRequest.class);
            assert actuatorRequest != null;
            actuatorRequest.onProcess(msg, channel);
        }
        log.info("客户端接收到消息: {}, 已接收消息总数: {}", content, ++this.count);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("------------------------ client channelRead ------------------------");
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("------------------------ client exceptionCaught ------------------------");
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client handlerRemoved ------------------------");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelUnregistered ------------------------");
        ++this.count;
        log.info("count = {}", this.count);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelRegistered ------------------------");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelInactive ------------------------");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelReadComplete ------------------------");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("------------------------ client userEventTriggered ------------------------");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.info("------------------------ client channelWritabilityChanged ------------------------");
    }

    private void pong(Channel channel) {
        MessageProtocolPoJo.MessageProtocol message = MessageProtocolPoJo.MessageProtocol
                .newBuilder()
                .setLen(PingPongUtils.PONG.getBytes(PingPongUtils.CHARSET).length)
                .setContent(PingPongUtils.PONG)
                .build();
        channel.writeAndFlush(message);
    }
}
