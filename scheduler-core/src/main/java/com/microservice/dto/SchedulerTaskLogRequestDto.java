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
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerTaskLogRequestDto extends BasePage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;

    /**
     * 执行器主键ID
     */
    private Integer logRegistryDetailId;

    /**
     * 任务，主键ID
     */
    private Integer logTaskId;

    /**
     * 0: cron任务 1: given任务
     */
    private String logTaskType;

    /**
     * 执行器地址，本次执行的地址
     */
    private String logExecutorAddress;

    /**
     * 执行器任务handler
     */
    private String logExecutorHandler;

    /**
     * 执行器任务参数
     */
    private String logExecutorParam;

    /**
     * 调度-时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime logTriggerTime;

    /**
     * 调度-结果
     */
    private String logTriggerCode;

    /**
     * 调度-日志
     */
    private String logTriggerMsg;

    /**
     * 执行-时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime logHandleTime;

    /**
     * 执行-状态
     */
    private String logHandleCode;

    /**
     * 执行-日志
     */
    private String logHandleMsg;

    /**
     * 告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败
     */
    private String logAlarmStatus;

    /**
     * 日志生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime logCreateTime;

    /**
     * ---------------------------------------------- cron task 相关 ----------------------------------------
     */
    private Integer taskCronId;

    /**
     * registry表app_name
     */
    private String taskCronAppName;

    /**
     * 中文描述
     */
    private String taskCronDesc;

    /**
     * 任务处理方法名
     */
    private String taskCronJobHandler;

    /**
     * cron表达式
     */
    private String taskCronExpression;

    /**
     * 0: stop 1: running
     */
    private String taskCronStatus;

    /**
     * 任务生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime taskCronCreateTime;

    /**
     * 0: 不写日志 1：写日志
     */
    private String taskCronWriteLog;

    /**
     * --------------------------------------------------- given 相关 ---------------------------------------
     */
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
}
