package com.microservice.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.microservice.utils.ErrCode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author zhangwei
 * @date 2021-03-14
 * <p>
 */
@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class JsonResult implements java.io.Serializable {
    private int code;
    private String msg;
    private Object data;

    public static JsonResult error() {
        return JsonResult.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .msg(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    public static JsonResult error(String message) {
        return JsonResult.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .msg(message)
                .build();
    }

    public static JsonResult success(String message) {
        return JsonResult.builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .build();
    }

    public static JsonResult success(Object data, String message) {
        if (!Strings.isNullOrEmpty(message)) {
            return JsonResult.builder()
                    .code(HttpStatus.OK.value())
                    .msg(message)
                    .data(data)
                    .build();
        }
        return JsonResult.builder()
                .code(HttpStatus.OK.value())
                .msg(ErrCode.SUCCESS)
                .data(data)
                .build();
    }

    public static JsonResult success() {
        return JsonResult.builder()
                .code(HttpStatus.OK.value())
                .msg(ErrCode.SUCCESS)
                .build();
    }

    @JsonIgnore
    public boolean isOk() {
        return this.code == HttpStatus.OK.value();
    }
}
