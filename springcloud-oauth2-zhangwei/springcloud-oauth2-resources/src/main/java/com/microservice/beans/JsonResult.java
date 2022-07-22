package com.microservice.beans;

import lombok.Data;

/**
 * @author: zhangwei
 * @date: 2022/7/22
 */
@Data
public class JsonResult<T> {
    private boolean flag;
    private Integer code;
    private String error;
    private T data;
}
