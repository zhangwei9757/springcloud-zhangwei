package com.microservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangwei
 * @date 2020-06-09
 * <p>
 */
@Data
public class BasePage implements Serializable {

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页数量
     */
    private Integer limit = 10;
}