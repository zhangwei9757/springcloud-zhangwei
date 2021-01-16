package com.microservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.entity.SchedulerTaskLog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-16
 */
public interface ISchedulerTaskLogService extends IService<SchedulerTaskLog> {

    IPage<SchedulerTaskLogRequestDto> findTaskLogsByParams(SchedulerTaskLogRequestDto taskLogRequestDto);
}
