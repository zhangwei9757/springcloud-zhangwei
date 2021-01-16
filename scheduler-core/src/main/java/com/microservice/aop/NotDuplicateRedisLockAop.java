package com.microservice.aop;

import com.microservice.exception.CacheLockException;
import com.microservice.annotation.lock.CacheLock;
import com.microservice.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.types.Expiration;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author zhangwei
 * @date 2020-02-16
 * <p>
 * redis 分布式锁监测 AOP
 */
@Aspect
@Configuration
@Slf4j
public class NotDuplicateRedisLockAop {

    @Resource
    private CacheKeyGenerator cacheKeyGenerator;

    @Resource
    private RedisUtil redisAbsentUtils;

    @Around("@annotation(com.microservice.annotation.lock.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);

        if (StringUtils.isEmpty(lock.prefix())) {
            throw new RuntimeException("lock key can't be null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);

        long currentTimeMillis = System.currentTimeMillis();
        String now = String.valueOf(currentTimeMillis);

        // 对于当前时间戳，本次操作真实过期超时时间
        Expiration expiration = Expiration.from(lock.expire(), lock.timeUnit());
        long expirationTimeInMilliseconds = expiration.getExpirationTimeInMilliseconds();
        long realTimeOutMillis = currentTimeMillis + expirationTimeInMilliseconds;
        String timeStempStr = String.valueOf(realTimeOutMillis);
        try {

            boolean success = redisAbsentUtils.locked(lockKey, timeStempStr);
            if (!success) {
                throw new CacheLockException("处理中!!!");
            }

            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常!!!");
            }
        } finally {
            redisAbsentUtils.unlocked(lockKey, timeStempStr);
        }
    }
}
