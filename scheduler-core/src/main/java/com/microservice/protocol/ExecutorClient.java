package com.microservice.protocol;

import com.microservice.annotation.EnableExecutorClient;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.handler.ExecutorJobManagerHandler;
import com.microservice.handler.retry.RetryPolicy;
import com.microservice.redis.RedisDefaultClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Data
@Accessors(chain = true)
@Component
@ConditionalOnBean(annotation = EnableExecutorClient.class)
public class ExecutorClient {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    public int retries = 0;
    public int retryServerAddressIndex = 0;

    @Resource
    private SchedulerConfigurationProperties properties;

    @Resource
    private RedisDefaultClientHandler defaultClientHandler;

    @Resource
    private RetryPolicy retryPolicy;

    @Resource
    private Executor executor;

    @Resource
    private ExecutorJobManagerHandler schedulerExecutorJobHandler;

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * 服务器代表启动
     * ChannelFuture channelFuture = bootstrap.connect(serverHost, serverPort).sync();
     * channel = channelFuture.channel();
     * log.info(">>> scheduler executor client started successfully, {}", channel.localAddress());
     * channelFuture.addListener(new ExecutorConnectionListener());
     * channelFuture.channel().closeFuture().sync();
     */
    public synchronized void run() {
        if (Objects.isNull(this.retryPolicy)) {
            throw new RuntimeException("RetryPolicy class is null...");
        }
        group = Objects.nonNull(group) ? group : new NioEventLoopGroup();

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ExecutorClientChannelInitializer(this));


            connect();
        } catch (Exception e) {
            log.error(">>> run 客户端自启动失败, 原因: {}", e.getLocalizedMessage(), e);
        }
    }

    public void connect() throws Exception {
        String balance = defaultClientHandler.nextBalance(defaultClientHandler.findNextBalanceKey());
        String[] ipPort = properties.findIpPort(balance);
        String serverAddress = ipPort[0];
        int serverPort = Integer.parseInt(ipPort[1]);
        log.info(">>> 客户端发起连接, connect => {}:{}", serverAddress, serverPort);

        ChannelFuture channelFuture = bootstrap
                .connect(serverAddress, serverPort)
                .addListener(new ExecutorConnectionListener());
        channel = channelFuture.channel();
        channelFuture.channel().closeFuture().sync();
    }

    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(group)) {
            group.shutdownGracefully();
            log.info(">>> destroy 客户端自消毁成功...");
        }
    }

    @PostConstruct()
    public void init() {
        defaultClientHandler.validateProperties(false);
        executor.execute(() -> {
            try {
                run();
            } catch (Exception e) {
                log.error(">>> init 客户端自启动失败, 原因: {}", e.getLocalizedMessage(), e);
            }
        });
    }
}
