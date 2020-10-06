package com.microservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p> 调度执行器任务
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutorJob {

    /**
     * executorJobHandler jobName
     */
    String value();

    /**
     * init handler, invoked when executorJobHandler init
     */
    String init() default "";

    /**
     * destroy handler, invoked when executorJobHandler destroy
     */
    String destroy() default "";
}
