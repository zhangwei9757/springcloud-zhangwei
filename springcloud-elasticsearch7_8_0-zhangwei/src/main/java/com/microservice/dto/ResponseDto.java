package com.microservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author zhangwei
 * @date 2020-07-01
 * <p>
 */
@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class ResponseDto implements Serializable {
    private int code;
    private String msg;
    private Object data;
    private long duration;

    public static ResponseDto error() {
        return ResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
    }

    public static ResponseDto error(String message, long duration) {
        return ResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg(message)
                .duration(duration)
                .build();
    }

    public static ResponseDto error(String message) {
        return ResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg(message)
                .build();
    }

    public static ResponseDto success(String message, Object data, long duration) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .data(data)
                .duration(duration)
                .build();
    }

    public static ResponseDto success(Object data, long duration) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg("操作成功")
                .data(data)
                .duration(duration)
                .build();
    }

    public static ResponseDto success(String message, Object data) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .data(data)
                .build();
    }

    public static ResponseDto success(String message) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .build();
    }

    public static ResponseDto success() {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .build();
    }
}
