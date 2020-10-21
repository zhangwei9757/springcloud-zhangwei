package com.microservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class SchedulerTaskCron extends Model<SchedulerTaskCron> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "task_cron_id", type = IdType.AUTO)
    private Integer taskCronId;

    /**
     * 执行器注册节点详细信息表主键id
     */
    private Integer taskCronRegistryDetailId;

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
    private LocalDateTime taskCronCreateTime;

    /**
     * 0: 不写日志 1：写日志
     */
    private String taskCronWriteLog;


    @Override
    protected Serializable pkVal() {
        return this.taskCronId;
    }

}
