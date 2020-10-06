package com.microservice.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ExecutorJobMessage implements Serializable {

    private static final long serialVersionUID = -1868644893629051744L;
    private String host;
    private String port;
    private LocalDateTime currentTime;

    private SchedulerConfigurationProperties properties;
}
