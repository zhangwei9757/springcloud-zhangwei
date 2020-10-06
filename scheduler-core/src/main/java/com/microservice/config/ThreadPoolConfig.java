package com.microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zw
 * @date 2019-09-08
 * <p>
 * 开启异步模式，配置线程池
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        executor.setCorePoolSize(13);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(30);
        //缓存队列
        executor.setQueueCapacity(13);
        //对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //允许的空闲时间
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("schedulerAsyncTask");
        executor.initialize();
        return executor;
    }
}