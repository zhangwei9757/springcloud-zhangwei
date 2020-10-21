package com.microservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.dto.SchedulerRegistryRequestDto;
import com.microservice.entity.SchedulerRegistry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
public interface SchedulerRegistryMapper extends BaseMapper<SchedulerRegistry> {

    List<SchedulerRegistryRequestDto> findRegistryByAppName(@Param("param") SchedulerRegistryRequestDto query);

    List<SchedulerRegistryDetailRequestDto> allActuatorsPage(@Param("page") IPage<SchedulerRegistryDetailRequestDto> iPage,
                                                             @Param("param") SchedulerRegistryDetailRequestDto registryDetailRequestDto);
}
