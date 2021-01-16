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
public class SchedulerTaskGiven extends Model<SchedulerTaskGiven> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "task_given_id", type = IdType.AUTO)
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
    private LocalDateTime taskGivenCreateTime;

    /**
     * 0: 不写日志 1：写日志
     */
    private String taskGivenWriteLog;

    @Override
    protected Serializable pkVal() {
        return this.taskGivenId;
    }

}
