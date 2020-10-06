package com.microservice.scheduler;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.utils.RedisUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 * 服务器代表，管理所有执行器微服务
 */
@Slf4j
@NoArgsConstructor
@Data
@Accessors(chain = true)
@Component
public class ExecutorGroupServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private long lastVoteTime;
    private RedisMessage leader;
    private RedisMessage currentServer;
    private Channel channel;

    @Resource
    private SchedulerConfigurationProperties properties;

    @Resource
    private Executor executor;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ExecutorGroupServerChannelInitializer executorGroupServerChannelInitializer;

    @Resource
    private RedisDefaultGroupServerHandler groupServerHandler;

    @Resource
    private RedisDefaultClientHandler clientHandler;

    /**
     * 服务器代表启动
     */
    public void run() throws Exception {
        clientHandler.validateProperties(true);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        int serverPort = properties.getServerPort();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, properties.getServerThreads())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(executorGroupServerChannelInitializer);

        ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();
        channelFuture.addListener(new ExecutorGroupServerChannelFutureListener());
        Channel channel = channelFuture.channel();
        channel.closeFuture().sync();
    }

    @PreDestroy
    public void destroy() {
//        if (Objects.nonNull(currentServer)) {
//            groupServerHandler.destroyLeaderAndMember(currentServer);
//            log.info(">>> destroy 服务器解除 clusterMember, Vote, Leader 状态成功...");
//        }
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
            log.info(">>> destroy 服务器自消毁 bossGroup 成功...");
        }
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully();
            log.info(">>> destroy 服务器自消毁 workerGroup 成功...");
        }
    }
}
