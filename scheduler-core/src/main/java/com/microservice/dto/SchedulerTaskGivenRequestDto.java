package com.microservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerTaskGivenRequestDto extends BasePage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer taskGivenId;

    /**
     * 执行器注册节点详细信息表主键id
     */
    private Integer taskGivenRegistryDetailId;

    /**
     * registry表app_name
     */
    private String taskGivenAppName;

    /**
     * 中文描述
     */
    private String taskGivenDesc;

    /**
     * 任务处理方法名
     */
    private String taskGivenJobHandler;

    /**
     * 执行参数
     */
    private String taskGivenParam;

    /**
     * 执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime taskGivenTime;

    /**
     * 延迟多少秒执行，配合执行时间使用
     */
    private String taskGivenDelayed;

    /**
     * 0: stop 1: running
     */
    private String taskGivenStatus;

    /**
     * 0: 未执行 1: 执行中 2: 待重试
     */
    private String taskGivenExecuteStatus;

    /**
     * 最近一次执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime taskGivenLastExecuteTime;

    /**
     * 重试次数
     */
    private Integer taskGivenRetryCount;

    /**
     * 重试间隔, 默认间隔三秒重试
     */
    private Integer taskGivenRetryDelayed;

    /**
     * 重试最大次数
     */
    private Integer taskGivenRetryMax;

    /**
     * 任务生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime taskGivenCreateTime;

    /**
     * 0: 不写日志 1：写日志
     */
    private String taskGivenWriteLog;

    /**
     * ----------------------------------------------- 回写执行状态信息 -------------------------------------
     */
    private LocalDateTime executeTime;
    private int code;
    private String msg;
    private String content;

    /**
     * ----------------------------------------------- 任务调度状态信息 -------------------------------------
     */
    private LocalDateTime triggerTime;
    private int triggerCode;
    private String triggerMsg;
}
