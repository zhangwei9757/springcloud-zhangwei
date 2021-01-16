package com.microservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.entity.SchedulerTaskGiven;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-19
 */
public interface ISchedulerTaskGivenService extends IService<SchedulerTaskGiven> {

    /**
     * 查询所有的任务，支持翻页
     *
     * @param taskRequest
     * @return
     */
    IPage<SchedulerTaskGivenRequestDto> tasks(SchedulerTaskGivenRequestDto taskRequest);

    /**
     * 获取所有任务
     *
     * @return
     */
    List<SchedulerTaskGivenRequestDto> tasks();

    /**
     * 修改执行状态
     *
     * @return
     */
    boolean updateGivenExecuteStatus(SchedulerTaskGivenRequestDto taskRequest);

    /**
     * 执行器注册指定时间的任务
     *
     * @param givenTask
     * @return
     */
    boolean registryGivenTask(SchedulerTaskGivenRequestDto givenTask);

    boolean delGivenTask(SchedulerTaskGivenRequestDto givenTask);

    List<SchedulerTaskGivenRequestDto> findGivenTasks(SchedulerTaskGivenRequestDto givenTask);

    boolean givenTaskStatusUpdate(SchedulerTaskGivenRequestDto givenRequestDto);
}
