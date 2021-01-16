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
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerRegistry extends Model<SchedulerRegistry> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "registry_id", type = IdType.AUTO)
    private Integer registryId;

    /**
     * 应用名，服务名
     */
    private String registryAppName;

    /**
     * 中文描述
     */
    private String registryDesc;

    /**
     * 自动注册时间
     */
    private LocalDateTime registryCreateTime;


    @Override
    protected Serializable pkVal() {
        return this.registryId;
    }

}
