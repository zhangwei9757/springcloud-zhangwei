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
public class SchedulerRegistryDetail extends Model<SchedulerRegistryDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "register_detail_id", type = IdType.AUTO)
    private Integer registerDetailId;

    /**
     * 主表id
     */
    private Integer registerDetailRegistryId;

    /**
     * register表app_name
     */
    private String registerDetailAppName;

    /**
     * ip
     */
    private String registerDetailIp;

    /**
     * port
     */
    private String registerDetailPort;

    /**
     * 注册序号
     */
    private Integer registerDetailSort;

    /**
     * 0：离线 1：在线， 默认离线
     */
    private String registerDetailStatus;

    /**
     * 上线时间
     */
    private LocalDateTime registerDetailOnlineTime;

    /**
     * 离线时间
     */
    private LocalDateTime registerDetailOfflineTime;

    /**
     * 创建时间
     */
    private LocalDateTime registerDetailCreateTime;


    @Override
    protected Serializable pkVal() {
        return this.registerDetailId;
    }

}
