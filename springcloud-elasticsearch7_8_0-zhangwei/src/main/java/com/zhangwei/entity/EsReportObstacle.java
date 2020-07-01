package com.zhangwei.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2020-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EsReportObstacle extends Model<EsReportObstacle> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单号
     */
    @TableField("troubleNo")
    private Integer troubleNo;

    /**
     * 问题标题
     */
    @TableField("problemTitle")
    private String problemTitle;

    /**
     * 问题描述
     */
    @TableField("problemDesc")
    private String problemDesc;

    /**
     * 生成单时间
     */
    @TableField("troubleTime")
    private Integer troubleTime;

    /**
     * 系统名
     */
    @TableField("systemName")
    private String systemName;

    /**
     * 模块名
     */
    @TableField("moduleName")
    private String moduleName;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
