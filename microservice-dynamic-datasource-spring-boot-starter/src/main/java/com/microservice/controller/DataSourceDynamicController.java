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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DataSourceDynamicController {

    @Resource
    private DataSource dataSource;

    @Resource
    private DataSourceCreator dataSourceCreator;

    @PostMapping(value = "/add")
    @ApiOperation("动态添加数据源")
    public List<DruidDataSourceDto> add(@Validated @RequestBody DataSourceProperties properties) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(properties, dataSourceProperty);

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(properties.getPoolName(), dataSource);
        return this.list();
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("动态删除数据源")
    public String remove(String name) {
        List<DruidDataSourceDto> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return "未发现数据库";
        }

        long master = list.stream()
                .filter(f -> !Objects.deepEquals(name.toLowerCase().trim(), f.getDataSourceName()) && Objects.deepEquals("master", f.getGroupName()))
                .count();
        if (master <= 0) {
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

    @GetMapping(value = "/list")
    @ApiOperation("动态查询所有数据源")
    public List<DruidDataSourceDto> list() {
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        Map<String, DynamicGroupDataSource> currentGroupDataSources = dynamicRoutingDataSource.getCurrentGroupDataSources();
        Map<String, DataSource> currentDataSources = dynamicRoutingDataSource.getCurrentDataSources();

        List<DruidDataSourceDto> ddss = new ArrayList<>(currentDataSources.size());

        // 找出各自的组
        currentDataSources.forEach((dataSourceName, value1) -> {
            // 数据库
            DruidDataSource dataSource = (DruidDataSource) value1;

            if (CollectionUtils.isEmpty(currentGroupDataSources)) {
                // 组成数据
                DruidDataSourceDto dataSourceDto = new DruidDataSourceDto();
                dataSourceDto.setGroupName(null);
                dataSourceDto.setDataSourceName(dataSourceName);
                dataSourceDto.setDbType(dataSource.getDbType());
                dataSourceDto.setDriverClass(dataSource.getDriverClassName());
                dataSourceDto.setUserName(dataSource.getUsername());
                dataSourceDto.setJdbcUrl(dataSource.getRawJdbcUrl());

                ddss.add(dataSourceDto);
            } else {
                currentGroupDataSources.forEach((key, value) -> {
                    String groupName = value.getGroupName();
                    List<DataSource> dss = value.getDataSources();
                    List<DataSource> collect = dss.stream()
                            .filter(f -> Objects.deepEquals(((DruidDataSource) f).getRawJdbcUrl(), dataSource.getRawJdbcUrl()))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        collect.forEach(source -> {
                            // 组成数据
                            DruidDataSource dataSource1 = (DruidDataSource) source;

                            DruidDataSourceDto dataSourceDto = new DruidDataSourceDto();
                            dataSourceDto.setGroupName(groupName);
                            dataSourceDto.setDataSourceName(dataSourceName);
                            dataSourceDto.setDbType(dataSource1.getDbType());
                            dataSourceDto.setDriverClass(dataSource1.getDriverClassName());
                            dataSourceDto.setUserName(dataSource.getUsername());
                            dataSourceDto.setJdbcUrl(dataSource1.getRawJdbcUrl());

                            ddss.add(dataSourceDto);
                        });
                    }
                });
            }
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
