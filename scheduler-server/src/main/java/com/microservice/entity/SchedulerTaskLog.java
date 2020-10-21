package com.microservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class SchedulerTaskLog extends Model<SchedulerTaskLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
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
    private LocalDateTime logCreateTime;


    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}
