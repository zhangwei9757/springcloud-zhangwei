package com.microservice.handler.actuator;

import com.microservice.dto.SchedulerRegistryDetailRequestDto;

/**
 * @author zw
 * @date 2020-10-19
 * <p> 注册，注销  执行器
 */
public interface RegistryDeRegistryActuator {

    boolean registryActuator(SchedulerRegistryDetailRequestDto registryDetailDto);

    boolean deRegistryActuator(SchedulerRegistryDetailRequestDto registryDetailDto);
}
