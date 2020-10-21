package com.microservice.service;

import com.microservice.dto.SchedulerRegistryRequestDto;
import com.microservice.entity.SchedulerRegistry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
public interface ISchedulerRegistryService extends IService<SchedulerRegistry> {

    /**
     * 查询指定执行器的注册信息，包含其节点列表清单
     * @param query
     * @return
     */
    List<SchedulerRegistryRequestDto> findRegistryByAppName(SchedulerRegistryRequestDto query);

    /**
     * 修改或者保存
     * @param dest
     * @return
     */
    boolean saveOrUpdate(SchedulerRegistryRequestDto dest);
}
