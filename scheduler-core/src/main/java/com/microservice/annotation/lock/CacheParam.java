package com.microservice.annotation.lock;

import java.lang.annotation.*;

/**
 * @author zhangwei
 * @date 2019-12-27
 * <p>
 * 分布式锁 参数名 [必填]
 * 参数类型一： 形参
 * 参数类型二： 形参属性之一
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {

    /**
     * 字段名称
     */
    String value() default "";
}