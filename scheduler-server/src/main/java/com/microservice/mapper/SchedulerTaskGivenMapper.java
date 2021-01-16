package com.microservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.entity.SchedulerTaskGiven;
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
public interface SchedulerTaskGivenMapper extends BaseMapper<SchedulerTaskGiven> {

    List<SchedulerTaskGivenRequestDto> tasksByParams(@Param("page") IPage<SchedulerTaskGivenRequestDto> iPage,
                                                     @Param("param") SchedulerTaskGivenRequestDto taskRequest);
}
