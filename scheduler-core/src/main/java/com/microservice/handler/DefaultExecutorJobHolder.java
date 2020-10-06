package com.microservice.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p> 执行器执行管理器
 */
@Slf4j
public class DefaultExecutorJobHolder {

    /**
     * job handler repository，后续需要使用分布式
     */
    private static ConcurrentMap<String, AbstractJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    /**
     * 注册执行器执行任务的方法，后续需要使用分布式
     *
     * @param jobName
     * @param executorJobHandler
     * @return
     */
    public static AbstractJobHandler registExecutorJobHandler(String jobName, AbstractJobHandler executorJobHandler) {
        log.info(">>>>>>>>>>> executor-job register executorJobHandler success, jobName:{}, executorJobHandler:{}", jobName, executorJobHandler);
        AbstractJobHandler jobHandler = jobHandlerRepository.put(jobName, executorJobHandler);
        jobHandlerRepository.forEach((k, v) -> {
            log.info(">>> jobName: {}, Methods: {}", k, v);
        });
        return jobHandler;
    }

    /**
     * 找出对应执行器的具体执行方法
     *
     * @param jobName
     * @return
     */
    public static AbstractJobHandler loadExecutorJobHandler(String jobName) {
        return jobHandlerRepository.get(jobName);
    }
}
