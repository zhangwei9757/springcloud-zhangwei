package com.microservice.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zw
 * @date 2022-01-14
 * <p>
 */
@Data
public class Account implements Serializable {

    private Long accountId;
    private String accountName;
}
