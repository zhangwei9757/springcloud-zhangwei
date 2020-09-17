package com.zhangwei.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangwei
 * @date 2020-09-14
 * <p> 协议相关
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class FlaskRequestDto {

    /**
     * 协议类型
     */
    private String seq;
    /**
     * 回调路径
     */
    private String callbackUrl;

    /**
     * 回调参数
     */
    private String params;

    @JsonIgnore
    public String processParams(String... args) {
        return String.join("|", args);
    }
}
