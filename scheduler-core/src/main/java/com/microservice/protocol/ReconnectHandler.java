package com.microservice.protocol;

import com.microservice.handler.retry.DefaultRetryPolicy;
import com.microservice.handler.retry.RetryPolicy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@ChannelHandler.Sharable
@Slf4j
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private ExecutorClient executorClient;

    public ReconnectHandler(ExecutorClient executorClient) {
        this.executorClient = executorClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        executorClient.retries = 0;
        log.info(">>> ReconnectHandler.channelActive 成功与服务器建立连接, 已重试{}次", executorClient.retries);
        ctx.fireChannelActive();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (executorClient.retries == 0) {
            log.info(">>> ReconnectHandler.channelUnregistered 成功与服务器断开连接, 已重试{}次", executorClient.retries);
            ctx.close();
        }

        boolean allowRetry = getRetryPolicy().allowRetry(executorClient.retries);
        if (allowRetry) {
            long sleepTimeMs = getRetryPolicy().getSleepTimeMs(executorClient.retries);
            log.info(">>> {}之后尝试重新连接到服务器, 当前重试次数: {}", sleepTimeMs, ++executorClient.retries);

            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> {
                try {
                    log.info(">>> ReconnectHandler.channelUnregistered  开始自动重连...");
                    executorClient.connect();
                } catch (Exception e) {
                    log.error(">>> ReconnectHandler.channelUnregistered  自动重连失败, 原因: {}", e.getLocalizedMessage());
                }
            }, sleepTimeMs, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelUnregistered();
    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        if (retries == 0) {
//            System.err.println("Lost the TCP connection with the server.");
//            ctx.close();
//        }
//
//        boolean allowRetry = getRetryPolicy().allowRetry(retries);
//        if (allowRetry) {
//
//            long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);
//
//            System.out.println(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, ++retries));
//
//            final EventLoop eventLoop = ctx.channel().eventLoop();
//            eventLoop.schedule(() -> {
//                System.out.println("Reconnecting ...");
//                executorClient.run();
//            }, sleepTimeMs, TimeUnit.MILLISECONDS);
//        }
//        ctx.fireChannelInactive();
//    }

    private synchronized RetryPolicy getRetryPolicy() {
        if (null == this.executorClient) {
            return new DefaultRetryPolicy();
        }
        return executorClient.getRetryPolicy();
    }
}