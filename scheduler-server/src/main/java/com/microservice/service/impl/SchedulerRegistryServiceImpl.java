package com.microservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.microservice.dto.SchedulerRegistryRequestDto;
import com.microservice.entity.SchedulerRegistry;
import com.microservice.mapper.SchedulerRegistryMapper;
import com.microservice.service.ISchedulerRegistryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
@Service
public class SchedulerRegistryServiceImpl extends ServiceImpl<SchedulerRegistryMapper, SchedulerRegistry> implements ISchedulerRegistryService {

    @Autowired
    private SchedulerRegistryMapper registryMapper;

    @Override
    public List<SchedulerRegistryRequestDto> findRegistryByAppName(SchedulerRegistryRequestDto query) {
        if (Objects.isNull(query)) {
            return Lists.newArrayList();
        }
        return registryMapper.findRegistryByAppName(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(SchedulerRegistryRequestDto dest) {
        if (Objects.isNull(dest)) {
            return true;
        }
        SchedulerRegistry schedulerRegistry = new SchedulerRegistry();
        BeanUtils.copyProperties(dest, schedulerRegistry);
        return this.saveOrUpdate(schedulerRegistry);
    }
}
