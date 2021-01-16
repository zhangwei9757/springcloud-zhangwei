package com.microservice.server;

import com.microservice.handler.retry.AbstractRetryPolicy;
import com.microservice.handler.retry.RetryPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2020-09-22
 * <p>
 */
@Component
@Primary
@Slf4j
public class TestRretryPolicy extends AbstractRetryPolicy implements RetryPolicy {

    public TestRretryPolicy() {
        this.baseSleepTimeMs = 2 * 1000L;
        this.maxRetries = 10;
        this.maxSleepMs = 20 * 1000;
    }

    public TestRretryPolicy(int baseSleepTimeMs, int maxRetries) {
        this(baseSleepTimeMs, maxRetries, DEFAULT_MAX_SLEEP_MS);
    }

    public TestRretryPolicy(int baseSleepTimeMs, int maxRetries, int maxSleepMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
        this.maxSleepMs = maxSleepMs;
    }

    @Override
    public boolean allowRetry(int retryCount) {
        return retryCount < maxRetries;
    }

    @Override
    public long getSleepTimeMs(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("重试次数必须>0");
        }
        if (retryCount > MAX_RETRIES_LIMIT) {
            log.info(">>> 当前已重试次数：{}, 最大重试次数为: {}", retryCount, MAX_RETRIES_LIMIT);
            retryCount = MAX_RETRIES_LIMIT;
        }
        long sleepMs = baseSleepTimeMs * Math.max(1, random.nextInt(1 << retryCount));
        if (sleepMs > maxSleepMs) {
            log.info(">>> 当前间隔时间: {}, 间隔时间超出阈值: {}, 使用最大间隔时间: {}", sleepMs, maxSleepMs, maxSleepMs);
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }
}
