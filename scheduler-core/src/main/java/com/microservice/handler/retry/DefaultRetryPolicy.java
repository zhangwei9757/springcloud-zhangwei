package com.microservice.handler.retry;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangwei
 * @date 2020-09-22
 * <p> 失败重连默认实现
 */
@Slf4j
public class DefaultRetryPolicy extends AbstractRetryPolicy implements RetryPolicy {

    public DefaultRetryPolicy() {
        this.baseSleepTimeMs = 5 * 1000L;
        this.maxRetries = 15;
        this.maxSleepMs = 15 * 1000;
    }

    public DefaultRetryPolicy(int baseSleepTimeMs, int maxRetries) {
        this(baseSleepTimeMs, maxRetries, DEFAULT_MAX_SLEEP_MS);
    }

    public DefaultRetryPolicy(int baseSleepTimeMs, int maxRetries, int maxSleepMs) {
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
            throw new IllegalArgumentException("The number of retries must be greater than 0");
        }
        if (retryCount > MAX_RETRIES_LIMIT) {
            retryCount = MAX_RETRIES_LIMIT;
        }
        long sleepMs = baseSleepTimeMs * Math.max(1, random.nextInt(1 << retryCount));
        if (sleepMs > maxSleepMs) {
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }
}
