package com.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangwei
 * @date 2020-09-11
 * <p>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class FlaskCallBackDto {

    /**
     * 协议类型
     */
    private String seq;
    /**
     * 0:失败 1：成功
     */
    private String handleStatus;
    /**
     * 返回消息
     */
    private String message;

    /**
     * 请求参数，原路返回
     */
    private String params;

    @JsonIgnore
    public String[] analysisParams(String params) {
        return params.split("\\|");
    }
}
