package com.microservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.entity.SchedulerRegistryDetail;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
public interface ISchedulerRegistryDetailService extends IService<SchedulerRegistryDetail> {
    /**
     * 修改或者保存
     *
     * @param dest
     * @return
     */
    boolean saveOrUpdate(SchedulerRegistryDetailRequestDto dest);

    /**
     * 全量修改数据，空值也保存，有ID主键即可
     *
     * @param schedulerRegistryDetailDto
     * @return
     */
    boolean updateAllowEmpty(SchedulerRegistryDetailRequestDto schedulerRegistryDetailDto);

    /**
     * 通过应用名，ip，port 查询对应注册节点信息
     *
     * @param schedulerRegistryDetailDto
     * @return
     */
    SchedulerRegistryDetailRequestDto findRegistryDetailByAppNameAndIpAndPort(SchedulerRegistryDetailRequestDto schedulerRegistryDetailDto);

    /**
     * 获取所有的执行器集群列表信息
     *
     * @param registryDetailRequestDto
     * @return
     */
    IPage<SchedulerRegistryDetailRequestDto> allActuatorsPage(SchedulerRegistryDetailRequestDto registryDetailRequestDto);

    /**
     * 所有执行器集群列表
     *
     * @return
     */
    List<SchedulerRegistryDetailRequestDto> allActuators();

    void autoFlushRedis();
}
