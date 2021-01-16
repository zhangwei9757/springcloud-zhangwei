package com.microservice.bean;

import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zw
 * @date 2020-10-06
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SchedulerTaskMessage implements Serializable {

    private static final long serialVersionUID = 1944620485172562397L;
    /**
     * 是表达式任务
     */
    private boolean cron;
    /**
     * cron 表达式执行
     */
    private String schedulerCron;
    /**
     * 特定执行时间
     */
    private LocalDateTime schedulerGivenTime;
    /**
     * 特定执行多少秒之后, 默认：不延迟
     */
    private long schedulerGivenDelay = 0;
    /**
     * 执行器名字
     */
    private String appName;
    /**
     * 执行器所在服务器名下，channel对应的 key
     */
    private String actuatorKey;
    /**
     * 执行器对应的地址信息
     */
    private String executorAddress;

    private String jobHandler;
    /**
     * 执行参数
     */
    private String jobHandlerParam;

    private SchedulerTaskCronRequestDto taskCronDto;
    private SchedulerTaskGivenRequestDto taskGivenDto;
}
