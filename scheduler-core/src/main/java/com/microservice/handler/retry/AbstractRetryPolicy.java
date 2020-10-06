package com.microservice.handler.retry;

import java.util.Random;

/**
 * @author zhangwei
 * @date 2020-09-22
 * <p>
 */
public abstract class AbstractRetryPolicy {

    protected final Random random = new Random();
    protected long baseSleepTimeMs;
    protected int maxRetries;
    protected int maxSleepMs;
}
