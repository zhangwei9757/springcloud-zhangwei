package com.microservice.bean;

import lombok.Data;

import java.util.List;

/**
 * @author zw
 * @date 2021-02-04
 * <p> 一键布署
 */
@Data
public class OneClickDeployment {
    /**
     * 自动布署数据源
     */
    private List<DataSourceProperties> dataSources;
    /**
     * 新主库名称
     */
    private String primary;
    /**
     * 要删除的库
     */
    private List<String> deleteDataSourceNames;
}
