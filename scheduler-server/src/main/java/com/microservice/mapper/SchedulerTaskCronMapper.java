package com.microservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.entity.SchedulerTaskCron;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-19
 */
public interface SchedulerTaskCronMapper extends BaseMapper<SchedulerTaskCron> {

    List<SchedulerTaskCronRequestDto> tasksByParams(@Param("page") IPage<SchedulerTaskCronRequestDto> iPage,
                                                    @Param("param") SchedulerTaskCronRequestDto taskRequest);
}
