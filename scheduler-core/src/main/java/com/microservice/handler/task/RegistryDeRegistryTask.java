package com.microservice.handler.task;

import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;

/**
 * @author zw
 * @date 2020-10-19
 * <p> 添加，删除 cron given 任务
 */
public interface RegistryDeRegistryTask {

    boolean registry(SchedulerTaskCronRequestDto cronTask);

    boolean registry(SchedulerTaskGivenRequestDto givenTask);

    boolean deRegistry(SchedulerTaskCronRequestDto cronTask);

    boolean deRegistry(SchedulerTaskGivenRequestDto givenTask);
}
