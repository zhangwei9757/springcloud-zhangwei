package com.microservice.annotation.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * @author zhangwei
 * @date 2019-12-27
 * <p>
 * redis 分布式锁, 配置参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * redis 锁key的前缀                     [必填]
     */
    String prefix() default "";

    /**
     * 过期秒数,默认为6秒 与微服务熔断时间一致   [必填]
     */
    int expire() default 6;

    /**
     * 超时时间单位                           [必填]
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Key的分隔符（默认 :）
     */
    String delimiter() default ":";
}