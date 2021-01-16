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
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerRegistryDetailRequestDto extends BasePage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer registerDetailId;

    /**
     * 执行器注册节点详细信息表主键id
     */
    private Integer taskCronRegistryDetailId;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registerDetailOnlineTime;

    /**
     * 离线时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registerDetailOfflineTime;

    /**
     * 创建时间
     */
    private LocalDateTime registerDetailCreateTime;

    /**
     * ---------------------------------------------- 执行器主信息 -----------------------------
     */
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
     * 注册信息生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registryCreateTime;
}
