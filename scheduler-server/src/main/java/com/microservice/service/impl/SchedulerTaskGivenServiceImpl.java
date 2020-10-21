package com.microservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.entity.SchedulerTaskGiven;
import com.microservice.mapper.SchedulerTaskGivenMapper;
import com.microservice.service.ISchedulerTaskGivenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-19
 */
@Service
@CacheConfig(cacheNames = "SchedulerTaskGivenServiceImpl")
@Slf4j
public class SchedulerTaskGivenServiceImpl extends ServiceImpl<SchedulerTaskGivenMapper, SchedulerTaskGiven> implements ISchedulerTaskGivenService {

    @Autowired
    private SchedulerTaskGivenMapper taskGivenMapper;

    @Override
    public IPage<SchedulerTaskGivenRequestDto> tasks(SchedulerTaskGivenRequestDto taskRequest) {
        IPage<SchedulerTaskGivenRequestDto> iPage = new Page<>(taskRequest.getCurrent(), taskRequest.getLimit());
        List<SchedulerTaskGivenRequestDto> tasks = taskGivenMapper.tasksByParams(iPage, taskRequest);
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks = tasks.stream()
                    .sorted(Comparator.comparing(SchedulerTaskGivenRequestDto::getTaskGivenAppName))
                    .collect(Collectors.toList());
        }
        iPage.setRecords(CollectionUtils.isEmpty(tasks) ? Lists.newArrayList() : tasks);
        return iPage;
    }

    @Override
    @Cacheable(key = "methodName")
    public List<SchedulerTaskGivenRequestDto> tasks() {
        List<SchedulerTaskGiven> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        List<SchedulerTaskGivenRequestDto> allTask = new ArrayList<>(list.size());
        list.parallelStream().forEach(f -> {
            SchedulerTaskGivenRequestDto task = new SchedulerTaskGivenRequestDto();
            BeanUtils.copyProperties(f, task);
            allTask.add(task);
        });
        return allTask;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateGivenExecuteStatus(SchedulerTaskGivenRequestDto taskRequest) {
        if (Objects.isNull(taskRequest)) {
            return true;
        }

        Integer taskGivenId = taskRequest.getTaskGivenId();
        if (Objects.isNull(taskGivenId) || taskGivenId <= 0) {
            return false;
        }

        try {
            String taskGivenExecuteStatus = taskRequest.getTaskGivenExecuteStatus();
            LocalDateTime taskGivenLastExecuteTime = taskRequest.getTaskGivenLastExecuteTime();
            Integer taskGivenRetryCount = taskRequest.getTaskGivenRetryCount();

            SchedulerTaskGiven taskGiven = new SchedulerTaskGiven();
            taskGiven.setTaskGivenId(taskRequest.getTaskGivenId());
            taskGiven.setTaskGivenExecuteStatus(taskGivenExecuteStatus);

            if (Objects.nonNull(taskGivenLastExecuteTime)) {
                taskGiven.setTaskGivenLastExecuteTime(taskGivenLastExecuteTime);
            }
            if (Objects.nonNull(taskGivenRetryCount) && taskGivenRetryCount > 0) {
                taskGiven.setTaskGivenRetryCount(taskGivenRetryCount);
            }

            return this.updateById(taskGiven);
        } catch (Exception e) {
            log.error(">>> 修改任务: {} 执行状态失败, 原因: {}", taskRequest.getTaskGivenId(), e.getLocalizedMessage(), e);
            return false;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean givenTaskStatusUpdate(SchedulerTaskGivenRequestDto givenRequestDto) {
        if (Objects.isNull(givenRequestDto)) {
            return true;
        }

        Integer taskGivenId = givenRequestDto.getTaskGivenId();
        if (Objects.isNull(taskGivenId) || taskGivenId <= 0) {
            return false;
        }

        try {
            SchedulerTaskGiven taskGiven = new SchedulerTaskGiven();
            taskGiven.setTaskGivenId(taskGivenId);
            taskGiven.setTaskGivenStatus(givenRequestDto.getTaskGivenStatus());

            String desc = givenRequestDto.getTaskGivenDesc();
            if (StringUtils.isNotBlank(desc)) {
                taskGiven.setTaskGivenDesc(desc);
            }
            String jobHandler = givenRequestDto.getTaskGivenJobHandler();
            if (StringUtils.isNotBlank(jobHandler)) {
                taskGiven.setTaskGivenJobHandler(jobHandler);
            }
            String param = givenRequestDto.getTaskGivenParam();
            taskGiven.setTaskGivenParam(param);

            LocalDateTime time = givenRequestDto.getTaskGivenTime();
            if (Objects.nonNull(time)) {
                taskGiven.setTaskGivenTime(time);
            }
            String delayed = givenRequestDto.getTaskGivenDelayed();
            if (StringUtils.isNotBlank(delayed)) {
                taskGiven.setTaskGivenDelayed(delayed);
            }
            Integer retryDelayed = givenRequestDto.getTaskGivenRetryDelayed();
            if (Objects.nonNull(retryDelayed)) {
                taskGiven.setTaskGivenRetryDelayed(retryDelayed);
            }
            Integer retryCount = givenRequestDto.getTaskGivenRetryCount();
            if (Objects.nonNull(retryCount)) {
                taskGiven.setTaskGivenRetryCount(retryCount);
            }
            Integer retryMax = givenRequestDto.getTaskGivenRetryMax();
            if (Objects.nonNull(retryMax)) {
                taskGiven.setTaskGivenRetryMax(retryMax);
            }
            String writeLog = givenRequestDto.getTaskGivenWriteLog();
            if (StringUtils.isNotBlank(writeLog)) {
                taskGiven.setTaskGivenWriteLog(writeLog);
            }
            String executeStatus = givenRequestDto.getTaskGivenExecuteStatus();
            if (StringUtils.isNotBlank(executeStatus)) {
                taskGiven.setTaskGivenExecuteStatus(executeStatus);
            }
            return this.updateById(taskGiven);
        } catch (Exception e) {
            log.error(">>> 修改given任务: {} ,运行状态失败, 原因: {}", givenRequestDto.getTaskGivenId(), e.getLocalizedMessage(), e);
            return false;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean registryGivenTask(SchedulerTaskGivenRequestDto givenTask) {
        if (Objects.nonNull(givenTask)) {
            SchedulerTaskGiven given = new SchedulerTaskGiven();
            BeanUtils.copyProperties(givenTask, given);
            return this.save(given);
        }
        return false;
    }

    @Override
    public boolean delGivenTask(SchedulerTaskGivenRequestDto givenTask) {
        if (Objects.isNull(givenTask)) {
            return true;
        }

        List<SchedulerTaskGivenRequestDto> givenTasks = this.findGivenTasks(givenTask);
        if (!CollectionUtils.isEmpty(givenTasks)) {
            Set<Integer> ids = givenTasks.parallelStream().map(SchedulerTaskGivenRequestDto::getTaskGivenId).collect(Collectors.toSet());
            int i = this.baseMapper.deleteBatchIds(ids);
            return i > 0 ? true : false;
        }
        return false;
    }

    @Override
    public List<SchedulerTaskGivenRequestDto> findGivenTasks(SchedulerTaskGivenRequestDto givenTask) {
        if (Objects.isNull(givenTask)) {
            return Lists.newArrayList();
        }

        QueryWrapper<SchedulerTaskGiven> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_given_app_name", givenTask.getTaskGivenAppName());
        queryWrapper.eq("task_given_job_handler", givenTask.getTaskGivenJobHandler());

        List<SchedulerTaskGiven> schedulerTaskGivens = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(schedulerTaskGivens)) {
            return Lists.newArrayList();
        }

        List<SchedulerTaskGivenRequestDto> list = new ArrayList<>(schedulerTaskGivens.size());
        schedulerTaskGivens.parallelStream().forEach(f -> {
            SchedulerTaskGivenRequestDto dto = new SchedulerTaskGivenRequestDto();
            BeanUtils.copyProperties(f, dto);
            list.add(dto);
        });
        return list;
    }
}
