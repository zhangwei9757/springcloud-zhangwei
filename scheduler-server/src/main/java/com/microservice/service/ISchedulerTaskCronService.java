package com.microservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.entity.SchedulerTaskCron;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-19
 */
public interface ISchedulerTaskCronService extends IService<SchedulerTaskCron> {

    /**
     * 查询所有的任务，支持翻页
     *
     * @param taskRequest
     * @return
     */
    IPage<SchedulerTaskCronRequestDto> tasks(SchedulerTaskCronRequestDto taskRequest);

    /**
     * 获取所有任务
     *
     * @return
     */
    List<SchedulerTaskCronRequestDto> tasks();

    /**
     * 执行器注册cron 任务
     *
     * @param cronRequestDto
     * @return
     */
    boolean registryCronTask(SchedulerTaskCronRequestDto cronRequestDto);

    boolean delCronTask(SchedulerTaskCronRequestDto cronRequestDto);

    public List<SchedulerTaskCronRequestDto> findCronTasks(SchedulerTaskCronRequestDto cron);

    boolean cronTaskStautsUpdate(SchedulerTaskCronRequestDto taskCronRequestDto);
}
