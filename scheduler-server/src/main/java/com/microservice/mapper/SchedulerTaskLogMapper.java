package com.microservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.entity.SchedulerTaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-16
 */
public interface SchedulerTaskLogMapper extends BaseMapper<SchedulerTaskLog> {

    List<SchedulerTaskLogRequestDto> findTaskLogsByParams(@Param("page") IPage<SchedulerTaskLogRequestDto> iPage,
                                                          @Param("param") SchedulerTaskLogRequestDto taskLog);
}
