package com.microservice.server;

import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.handler.task.AbstractRegistryDeRegistryTask;
import com.microservice.handler.task.RegistryDeRegistryTask;
import org.springframework.stereotype.Service;

/**
 * @author zw
 * @date 2020-10-19
 * <p>
 * 客户端自定义注册 cron , given 调度任务
 */
@Service
public class TestRegistryDeRegistryTask extends AbstractRegistryDeRegistryTask implements RegistryDeRegistryTask {

    @Override
    public boolean registry(SchedulerTaskCronRequestDto cronTask) {
        return super.registry(cronTask);
    }

    @Override
    public boolean registry(SchedulerTaskGivenRequestDto givenTask) {
        return super.registry(givenTask);
    }

    @Override
    public boolean deRegistry(SchedulerTaskCronRequestDto cronTask) {
        return super.deRegistry(cronTask);
    }

    @Override
    public boolean deRegistry(SchedulerTaskGivenRequestDto givenTask) {
        return super.deRegistry(givenTask);
    }
}
