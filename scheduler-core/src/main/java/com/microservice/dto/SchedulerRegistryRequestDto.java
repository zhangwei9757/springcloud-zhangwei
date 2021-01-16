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
import java.util.List;

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
public class SchedulerRegistryRequestDto extends BasePage implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /**
     * ---------------------------------------  节点列表详细信息 --------------------------------------
     */
    private List<SchedulerRegistryDetailRequestDto> registryDetails;
}
