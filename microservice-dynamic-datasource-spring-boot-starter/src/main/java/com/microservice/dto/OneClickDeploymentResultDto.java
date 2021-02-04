package com.microservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zw
 * @date 2021/2/4
 * <p> 一键布署结果
 */
@Data
public class OneClickDeploymentResultDto implements Serializable {
    private static final long serialVersionUID = -5414503119743649979L;

    private Object primary;
    private List<DruidDataSourceDto> dataSources;
}