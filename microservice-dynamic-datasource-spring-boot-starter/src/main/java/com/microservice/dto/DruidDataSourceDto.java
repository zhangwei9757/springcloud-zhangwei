package com.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zw
 * @date 2020-11-21
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DruidDataSourceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object primary;

    private String groupName;
    private String dataSourceName;

    private String userName;
    private String jdbcUrl;
    private String driverClass;
    private String dbType;
}
