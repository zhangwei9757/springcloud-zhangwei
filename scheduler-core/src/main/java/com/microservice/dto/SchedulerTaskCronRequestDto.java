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
public class SchedulerTaskCronRequestDto extends BasePage implements Serializable {

    private static final long serialVersionUID = 1L;

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
