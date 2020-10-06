package com.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author zhangwei
 * @date 2020-06-09
 * <p>
 */
@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class ResponseDto implements Serializable {
    private static final long serialVersionUID = 5910025195461331293L;
    private int code;
    private String msg;
    private Object data;

    public static ResponseDto error() {
        return ResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .msg(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    public static ResponseDto error(String message) {
        return ResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .msg(message)
                .build();
    }

    public static ResponseDto success(String message) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .build();
    }

    public static ResponseDto success(Object data) {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static ResponseDto success() {
        return ResponseDto.builder()
                .code(HttpStatus.OK.value())
                .msg("操作成功")
                .build();
    }

    @JsonIgnore
    public boolean isOk(){
        return this.code == HttpStatus.OK.value();
    }
}
