package com.microservice.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@Slf4j
public class ExecutorConnectionListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        Channel channel = channelFuture.channel();
        if (!channelFuture.isSuccess()) {
            log.info(">>> client operationComplete 客户端, 启动失败...");
            channel.pipeline().fireChannelInactive();
        } else {
            log.info(">>> client operationComplete 客户端[{}], 启动成功...", channel.localAddress());
            channel.pipeline().fireChannelActive();
        }
    }
}
