package com.microservice.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.entity.SchedulerTaskLog;
import com.microservice.mapper.SchedulerTaskLogMapper;
import com.microservice.service.ISchedulerTaskLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-16
 */
@Service
public class SchedulerTaskLogServiceImpl extends ServiceImpl<SchedulerTaskLogMapper, SchedulerTaskLog> implements ISchedulerTaskLogService {

    @Autowired
    private SchedulerTaskLogMapper taskLogMapper;

    @Override
    public IPage<SchedulerTaskLogRequestDto> findTaskLogsByParams(SchedulerTaskLogRequestDto taskLog) {
        IPage<SchedulerTaskLogRequestDto> iPage = new Page<>(taskLog.getCurrent(), taskLog.getLimit());
        List<SchedulerTaskLogRequestDto> tasks = taskLogMapper.findTaskLogsByParams(iPage, taskLog);
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks = tasks.stream()
                    .sorted(Comparator.comparing(SchedulerTaskLogRequestDto::getLogCreateTime))
                    .sorted(Comparator.comparing(SchedulerTaskLogRequestDto::getLogExecutorAddress))
                    .sorted(Comparator.comparing(SchedulerTaskLogRequestDto::getLogTaskType))
                    .collect(Collectors.toList());
        }
        iPage.setRecords(CollectionUtils.isEmpty(tasks) ? Lists.newArrayList() : tasks);
        return iPage;
    }
}
