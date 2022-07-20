package com.microservice.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author zhangwei
 * @date 2019-12-04
 * <p>
 * json Bean
 * 默认响应成功，状态码200
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class JsonResult {
    private boolean flag = true;
    private String errMessage = null;
    private int code = HttpStatus.OK.value();
    private Object result = null;

    public static JsonResult create() {
        return new JsonResult();
    }

    public static JsonResult success() {
        return create();
    }

    public static JsonResult success(Object result) {
        return create().setResult(result);
    }

    public static JsonResult success(HttpStatusMicro result) {
        return create().setCode(result.getCode()).setResult(result.getMessage());
    }

    public static JsonResult success(int code, Object result) {
        return create().setCode(code).setResult(result);
    }

    public static JsonResult fail(int code, String errMessage) {
        return create().setFlag(false).setCode(code).setErrMessage(errMessage);
    }

    public static JsonResult fail(HttpStatusMicro responseEnum) {
        return create().setFlag(false).setCode(responseEnum.getCode()).setErrMessage(responseEnum.getMessage());
    }

    public static JsonResult fail() {
        return create()
                .setFlag(false)
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setErrMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    public static JsonResult fail(String message) {
        return create()
                .setFlag(false)
                .setCode(HttpStatus.BAD_REQUEST.value())
                .setErrMessage(message);
    }
}