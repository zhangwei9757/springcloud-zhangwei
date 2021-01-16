package com.microservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.entity.SchedulerTaskCron;
import com.microservice.mapper.SchedulerTaskCronMapper;
import com.microservice.service.ISchedulerTaskCronService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
@CacheConfig(cacheNames = "SchedulerTaskCronServiceImpl")
@Slf4j
public class SchedulerTaskCronServiceImpl extends ServiceImpl<SchedulerTaskCronMapper, SchedulerTaskCron> implements ISchedulerTaskCronService {

    @Autowired
    private SchedulerTaskCronMapper taskCronMapper;

    @Override
    public IPage<SchedulerTaskCronRequestDto> tasks(SchedulerTaskCronRequestDto taskRequest) {
        IPage<SchedulerTaskCronRequestDto> iPage = new Page<>(taskRequest.getCurrent(), taskRequest.getLimit());
        List<SchedulerTaskCronRequestDto> tasks = taskCronMapper.tasksByParams(iPage, taskRequest);
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks = tasks.stream()
                    .sorted(Comparator.comparing(SchedulerTaskCronRequestDto::getTaskCronAppName))
                    .collect(Collectors.toList());
        }
        iPage.setRecords(CollectionUtils.isEmpty(tasks) ? Lists.newArrayList() : tasks);
        return iPage;
    }

    @Override
    @Cacheable(key = "methodName")
    public List<SchedulerTaskCronRequestDto> tasks() {
        List<SchedulerTaskCron> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        List<SchedulerTaskCronRequestDto> allTask = new ArrayList<>(list.size());
        list.parallelStream().forEach(f -> {
            SchedulerTaskCronRequestDto task = new SchedulerTaskCronRequestDto();
            BeanUtils.copyProperties(f, task);
            allTask.add(task);
        });
        return allTask;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean registryCronTask(SchedulerTaskCronRequestDto cronRequestDto) {
        if (Objects.nonNull(cronRequestDto)) {
            SchedulerTaskCron cron = new SchedulerTaskCron();
            BeanUtils.copyProperties(cronRequestDto, cron);
            return this.save(cron);
        }

        return false;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean cronTaskStautsUpdate(SchedulerTaskCronRequestDto taskCronRequestDto) {
        if (Objects.isNull(taskCronRequestDto)) {
            return true;
        }

        Integer taskCronId = taskCronRequestDto.getTaskCronId();
        if (Objects.isNull(taskCronId) || taskCronId <= 0) {
            return false;
        }

        try {
            SchedulerTaskCron taskCron = new SchedulerTaskCron();
            taskCron.setTaskCronId(taskCronId);
            taskCron.setTaskCronStatus(taskCronRequestDto.getTaskCronStatus());

            String desc = taskCronRequestDto.getTaskCronDesc();
            if (StringUtils.isNotBlank(desc)) {
                taskCron.setTaskCronDesc(desc);
            }
            String jobHandler = taskCronRequestDto.getTaskCronJobHandler();
            if (StringUtils.isNotBlank(jobHandler)) {
                taskCron.setTaskCronJobHandler(jobHandler);
            }
            String expression = taskCronRequestDto.getTaskCronExpression();
            if (StringUtils.isNotBlank(expression)) {
                taskCron.setTaskCronExpression(expression);
            }
            String writeLog = taskCronRequestDto.getTaskCronWriteLog();
            if (StringUtils.isNotBlank(writeLog)) {
                taskCron.setTaskCronWriteLog(writeLog);
            }
            return this.updateById(taskCron);
        } catch (Exception e) {
            log.error(">>> 修改cron任务: {} ,运行状态失败, 原因: {}", taskCronRequestDto.getTaskCronId(), e.getLocalizedMessage(), e);
            return false;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean delCronTask(SchedulerTaskCronRequestDto cronRequestDto) {
        if (Objects.isNull(cronRequestDto)) {
            return true;
        }

        List<SchedulerTaskCronRequestDto> cronTasks = this.findCronTasks(cronRequestDto);
        if (!CollectionUtils.isEmpty(cronTasks)) {
            Set<Integer> ids = cronTasks.parallelStream().map(SchedulerTaskCronRequestDto::getTaskCronId).collect(Collectors.toSet());
            int i = this.baseMapper.deleteBatchIds(ids);
            return i > 0;
        }
        return false;
    }

    @Override
    @Cacheable(key = "#{cron.hashCode()}")
    public List<SchedulerTaskCronRequestDto> findCronTasks(SchedulerTaskCronRequestDto cron) {
        if (Objects.isNull(cron)) {
            return Lists.newArrayList();
        }

        QueryWrapper<SchedulerTaskCron> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_cron_app_name", cron.getTaskCronAppName());
        queryWrapper.eq("task_cron_job_handler", cron.getTaskCronJobHandler());

        List<SchedulerTaskCron> taskCrons = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(taskCrons)) {
            return Lists.newArrayList();
        }

        List<SchedulerTaskCronRequestDto> list = new ArrayList<>(taskCrons.size());
        taskCrons.parallelStream().forEach(f -> {
            SchedulerTaskCronRequestDto dto = new SchedulerTaskCronRequestDto();
            BeanUtils.copyProperties(f, dto);
            list.add(dto);
        });
        return list;
    }
}
