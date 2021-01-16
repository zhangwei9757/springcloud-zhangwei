package com.microservice.handler.retry;

/**
 * @author zhangwei
 * @date 2020-09-22
 * <p> netty 重连策略
 */
public interface RetryPolicy {

    int MAX_RETRIES_LIMIT = 30;

    int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;

    /**
     * Called when an operation has failed for some reason. This method should return
     * true to make another attempt.
     *
     * @param retryCount the number of times retried so far (0 the first time)
     * @return true/false
     */
    boolean allowRetry(int retryCount);

    /**
     * get sleep time in ms of current retry count.
     *
     * @param retryCount current retry count
     * @return the time to sleep
     */
    long getSleepTimeMs(int retryCount);
}
