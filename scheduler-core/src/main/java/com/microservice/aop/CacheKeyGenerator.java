package com.microservice.aop;


import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author zhangwei
 * @date 2020-02-16
 * <p> key生成器
 */
public interface CacheKeyGenerator {

    /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
