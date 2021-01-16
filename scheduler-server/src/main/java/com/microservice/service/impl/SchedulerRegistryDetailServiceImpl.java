package com.microservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.entity.SchedulerRegistryDetail;
import com.microservice.exception.SchedulerCoreException;
import com.microservice.mapper.SchedulerRegistryDetailMapper;
import com.microservice.mapper.SchedulerRegistryMapper;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.utils.ErrCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "SchedulerRegistryDetailServiceImpl")
public class SchedulerRegistryDetailServiceImpl extends ServiceImpl<SchedulerRegistryDetailMapper, SchedulerRegistryDetail> implements ISchedulerRegistryDetailService {

    @Autowired
    private SchedulerRegistryMapper registryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(SchedulerRegistryDetailRequestDto dest) {
        if (Objects.isNull(dest)) {
            return true;
        }
        SchedulerRegistryDetail schedulerRegistryDetail = new SchedulerRegistryDetail();
        BeanUtils.copyProperties(dest, schedulerRegistryDetail);

        // 如果存在ID主键，为了保证把空值也保存进去，必须使用全保存
        Integer registerDetailId = schedulerRegistryDetail.getRegisterDetailId();
        if (Objects.nonNull(registerDetailId) && registerDetailId > 0) {
            // 使用全量修改空值也保存
            return this.updateAllAttr(schedulerRegistryDetail);
        }
        return this.saveOrUpdate(schedulerRegistryDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public boolean updateAllowEmpty(SchedulerRegistryDetailRequestDto schedulerRegistryDetailDto) {
        try {
            return this.updateAllAttr(schedulerRegistryDetailDto);
        } catch (Exception e) {
            log.error("SchedulerRegistryDetailServiceImpl.updateAllowEmpty: {}", e.getLocalizedMessage());
            throw new SchedulerCoreException(ErrCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public SchedulerRegistryDetailRequestDto findRegistryDetailByAppNameAndIpAndPort(SchedulerRegistryDetailRequestDto schedulerRegistryDetailDto) {
        if (Objects.isNull(schedulerRegistryDetailDto)) {
            return null;
        }

        QueryWrapper<SchedulerRegistryDetail> queryWrapper = new QueryWrapper<>();
        String registerDetailAppName = schedulerRegistryDetailDto.getRegisterDetailAppName();
        if (StringUtils.isNotBlank(registerDetailAppName)) {
            queryWrapper.eq("register_detail_app_name", registerDetailAppName);
        }

        String registerDetailIp = schedulerRegistryDetailDto.getRegisterDetailIp();
        if (StringUtils.isNotBlank(registerDetailIp)) {
            queryWrapper.eq("register_detail_ip", registerDetailIp);
        }

        String registerDetailPort = schedulerRegistryDetailDto.getRegisterDetailPort();
        if (StringUtils.isNotBlank(registerDetailPort)) {
            queryWrapper.eq("register_detail_port", registerDetailPort);
        }

        if (StringUtils.isAnyBlank(registerDetailAppName, registerDetailIp, registerDetailPort)) {
            throw new SchedulerCoreException(ErrCode.PARAMS_WRONGFUL);
        }

        SchedulerRegistryDetail detail = this.getBaseMapper().selectOne(queryWrapper);
        if (Objects.nonNull(detail)) {
            SchedulerRegistryDetailRequestDto registryDetailRequestDto = new SchedulerRegistryDetailRequestDto();
            BeanUtils.copyProperties(detail, registryDetailRequestDto);
            return registryDetailRequestDto;
        }
        return null;
    }

    /**
     * DTO 参数
     *
     * @param schedulerRegistryDetailDto
     * @return
     */
    private boolean updateAllAttr(SchedulerRegistryDetailRequestDto schedulerRegistryDetailDto) {
        SchedulerRegistryDetail schedulerRegistryDetail = new SchedulerRegistryDetail();
        schedulerRegistryDetail.setRegisterDetailId(schedulerRegistryDetailDto.getRegisterDetailId());

        if (null == schedulerRegistryDetail.getRegisterDetailId()) {
            throw new SchedulerCoreException(ErrCode.PARAMS_EMPTY);
        }

        UpdateWrapper<SchedulerRegistryDetail> updateWrapper = new UpdateWrapper<>(schedulerRegistryDetail);
        updateWrapper.set("register_detail_app_name", schedulerRegistryDetailDto.getRegisterDetailAppName());
        updateWrapper.set("register_detail_ip", schedulerRegistryDetailDto.getRegisterDetailIp());
        updateWrapper.set("register_detail_port", schedulerRegistryDetailDto.getRegisterDetailPort());
        updateWrapper.set("register_detail_sort", schedulerRegistryDetailDto.getRegisterDetailSort());
        updateWrapper.set("register_detail_status", schedulerRegistryDetailDto.getRegisterDetailStatus());
        updateWrapper.set("register_detail_online_time", schedulerRegistryDetailDto.getRegisterDetailOnlineTime());
        updateWrapper.set("register_detail_offline_time", schedulerRegistryDetailDto.getRegisterDetailOfflineTime());
        return this.update(updateWrapper);
    }

    /**
     * entity 参数
     *
     * @param temp
     * @return
     */
    private boolean updateAllAttr(SchedulerRegistryDetail temp) {
        SchedulerRegistryDetail schedulerRegistryDetail = new SchedulerRegistryDetail();
        schedulerRegistryDetail.setRegisterDetailId(temp.getRegisterDetailId());

        if (null == schedulerRegistryDetail.getRegisterDetailId()) {
            throw new SchedulerCoreException(ErrCode.PARAMS_EMPTY);
        }

        UpdateWrapper<SchedulerRegistryDetail> updateWrapper = new UpdateWrapper<>(schedulerRegistryDetail);
        updateWrapper.set("register_detail_app_name", temp.getRegisterDetailAppName());
        updateWrapper.set("register_detail_ip", temp.getRegisterDetailIp());
        updateWrapper.set("register_detail_port", temp.getRegisterDetailPort());
        updateWrapper.set("register_detail_sort", temp.getRegisterDetailSort());
        updateWrapper.set("register_detail_status", temp.getRegisterDetailStatus());
        updateWrapper.set("register_detail_online_time", temp.getRegisterDetailOnlineTime());
        updateWrapper.set("register_detail_offline_time", temp.getRegisterDetailOfflineTime());
        return this.update(updateWrapper);
    }

    @Override
    public IPage<SchedulerRegistryDetailRequestDto> allActuatorsPage(SchedulerRegistryDetailRequestDto registryDetailRequestDto) {
        IPage<SchedulerRegistryDetailRequestDto> iPage = new Page<>(registryDetailRequestDto.getCurrent(), registryDetailRequestDto.getLimit());
        List<SchedulerRegistryDetailRequestDto> tasks = registryMapper.allActuatorsPage(iPage, registryDetailRequestDto);
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks = tasks.parallelStream()
                    .sorted(Comparator.comparing(SchedulerRegistryDetailRequestDto::getRegistryAppName))
                    .sorted(Comparator.comparing(SchedulerRegistryDetailRequestDto::getRegisterDetailPort))
                    .collect(Collectors.toList());
        }
        iPage.setRecords(CollectionUtils.isEmpty(tasks) ? Lists.newArrayList() : tasks);
        return iPage;
    }

    @Override
    @Cacheable(key = "methodName")
    public List<SchedulerRegistryDetailRequestDto> allActuators() {

        List<SchedulerRegistryDetail> list = this.list();
        if (!CollectionUtils.isEmpty(list)) {
            List<SchedulerRegistryDetailRequestDto> rtns = new ArrayList<>(list.size());
            list.parallelStream().forEach(f -> {
                SchedulerRegistryDetailRequestDto dto = new SchedulerRegistryDetailRequestDto();
                BeanUtils.copyProperties(f, dto);
                rtns.add(dto);
            });
            return rtns.parallelStream()
                    .sorted(Comparator.comparing(SchedulerRegistryDetailRequestDto::getRegisterDetailAppName))
                    .sorted(Comparator.comparing(SchedulerRegistryDetailRequestDto::getRegisterDetailPort))
                    .collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    @Override
    @CacheEvict(allEntries = true)
    public void autoFlushRedis() {
        log.info(">>> autoFlushRedis 触发自动刷新执行器状态事件...");
    }
}
