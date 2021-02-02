package com.microservice.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicGroupDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.google.common.collect.Lists;
import com.microservice.bean.DataSourceProperties;
import com.microservice.dto.DruidDataSourceDto;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zw
 * @date 2020-11-21
 * <p>
 */
@RestController
@RequestMapping(value = "/dataSource")
@Slf4j
public class DataSourceDynamicController {

    @Resource
    private DataSource dataSource;

    @Resource
    private DataSourceCreator dataSourceCreator;

    @PostMapping(value = "/add")
    @ApiOperation("动态添加数据源")
    public String add(@Validated @RequestBody DataSourceProperties properties) {
        if (Objects.isNull(properties)) {
            return "参数不合法";
        }

        String poolName = properties.getPoolName();
        try {
            if (StringUtils.isEmpty(poolName)) {
                return "参数不合法";
            }
            List<DruidDataSourceDto> list = this.list();
            long count = list.parallelStream().filter(f -> Objects.deepEquals(poolName, f.getDataSourceName()))
                    .count();
            if (count > 0) {
                return "已存在数据源: " + poolName;
            }
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            BeanUtils.copyProperties(properties, dataSourceProperty);

            DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
            DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
            ds.addDataSource(poolName, dataSource);
        } catch (BeansException e) {
            log.error(">>> 动态添加数据源失败, 原因: {}", e.getLocalizedMessage(), e);
            return "添加失败, 原因: " + e.getLocalizedMessage();
        }

        List<DruidDataSourceDto> list = this.list();
        long count = list.parallelStream().filter(f -> Objects.deepEquals(poolName, f.getDataSourceName()))
                .count();
        return count == 1 ? "添加成功" : "添加失败";
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("动态删除数据源")
    public String remove(String name) {
        List<DruidDataSourceDto> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return "未发现数据库";
        }

        long master = list.stream()
                .filter(f -> Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()) && Objects.deepEquals("master", f.getGroupName()))
                .count();
        if (master > 0) {
            return String.format("一旦删除: %s, 将不存在主数据库，请先配置[新主数据库], 再删除：%s", name, name);
        }

        // 验证合法性
        DruidDataSourceDto druidDataSourceDto = list.stream()
                .filter(f -> Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()))
                .findFirst()
                .orElseGet((() -> null));
        if (Objects.isNull(druidDataSourceDto)) {
            return "未发现数据库: " + name;
        }

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        // 先停掉数据库， 再删除此条记录 ,删除操作自动联动操作
        ds.removeDataSource(name.toLowerCase().trim());

        // 再次验证合法性
        list = this.list();
        druidDataSourceDto = list.stream()
                .filter(f -> Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()))
                .findFirst()
                .orElseGet((() -> null));
        if (Objects.isNull(druidDataSourceDto)) {
            return "删除成功";
        }
        return "删除数据库: " + name + ", 失败";
    }

    @DeleteMapping(value = "/reSetPrimaryDataSource")
    @ApiOperation("重置主数据源")
    public String reSetPrimaryDataSource(String name) {
        List<DruidDataSourceDto> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return "未发现数据库";
        }

        long master = list.stream()
                .filter(f -> Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()) && Objects.deepEquals("master", f.getGroupName()))
                .count();
        if (master > 0) {
            return String.format("一旦删除: %s, 将不存在主数据库，请先配置[新主数据库], 再删除：%s", name, name);
        }

        // 验证合法性
        DruidDataSourceDto druidDataSourceDto = list.stream()
                .filter(f -> Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()))
                .findFirst()
                .orElseGet((() -> null));
        if (Objects.isNull(druidDataSourceDto)) {
            return "未发现数据库: " + name;
        }

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        // 先停掉数据库， 再删除此条记录 ,删除操作自动联动操作
        ds.setPrimary(name.toLowerCase().trim());
        return "删除数据库: " + name + ", 失败";
    }

    @GetMapping(value = "/list")
    @ApiOperation("动态查询所有数据源")
    public List<DruidDataSourceDto> list() {
        return findDataSourceList();
    }

    private List<DruidDataSourceDto> findDataSourceList() {
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        Map<String, DynamicGroupDataSource> currentGroupDataSources = dynamicRoutingDataSource.getCurrentGroupDataSources();
        Map<String, DataSource> currentDataSources = dynamicRoutingDataSource.getCurrentDataSources();

        List<DruidDataSourceDto> ddss = new ArrayList<>(currentDataSources.size());

        // 找出各自的组
        currentDataSources.forEach((dataSourceName, value1) -> {
            // 数据库
            DruidDataSource dataSource = (DruidDataSource) value1;

            DruidDataSourceDto dataSourceDto = new DruidDataSourceDto();
            dataSourceDto.setGroupName(null);
            dataSourceDto.setDataSourceName(dataSourceName);
            dataSourceDto.setDbType(dataSource.getDbType());
            dataSourceDto.setDriverClass(dataSource.getDriverClassName());
            dataSourceDto.setUserName(dataSource.getUsername());
            dataSourceDto.setJdbcUrl(dataSource.getRawJdbcUrl());

            String groupName = StringUtils.EMPTY;
            if (CollectionUtils.isEmpty(currentGroupDataSources)) {
                // 组成数据
                Map.Entry<String, DynamicGroupDataSource> dataSourceEntry = currentGroupDataSources.entrySet().stream().filter(f -> dataSourceName.contains(f.getKey()))
                        .findFirst().orElseGet(() -> null);
                if (Objects.nonNull(dataSourceEntry)) {
                    DynamicGroupDataSource value = dataSourceEntry.getValue();
                    groupName = value.getGroupName();
                }
            }
            dataSourceDto.setGroupName(groupName);
            ddss.add(dataSourceDto);
        });

        if (CollectionUtils.isEmpty(ddss)) {
            return Lists.newArrayList();
        }

        return ddss.parallelStream()
                .sorted(Comparator.comparing(DruidDataSourceDto::getGroupName))
                .sorted(Comparator.comparing(DruidDataSourceDto::getDataSourceName))
                .sorted(Comparator.comparing(DruidDataSourceDto::getDbType))
                .sorted(Comparator.comparing(DruidDataSourceDto::getUserName))
                .collect(Collectors.toList());
    }
}
