package com.microservice.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zw
 * @date 2020-10-05
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActuatorHealthCheckResult implements Serializable {

    private static final long serialVersionUID = -5217893528823189685L;
    private String status;
}
