package com.microservice.bean;

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
public class JsonResponse {
    private boolean flag = true;
    private String errMessage = null;
    private int code = HttpStatus.OK.value();
    private Object result = null;

    public static JsonResponse create() {
        return new JsonResponse();
    }

    public static JsonResponse success() {
        return create();
    }

    public static JsonResponse complete(String message) {
        return create().setErrMessage(message);
    }

    public static JsonResponse success(Object result) {
        return create().setResult(result);
    }

    public static JsonResponse success(int code, Object result) {
        return create().setCode(code).setResult(result);
    }

    public static JsonResponse fail(int code, String errMessage) {
        return create().setFlag(false).setCode(code).setErrMessage(errMessage);
    }

    public static JsonResponse fail(int code, String errMessage, Object result) {
        return create().setFlag(false).setCode(code).setErrMessage(errMessage).setResult(result);
    }

    public static JsonResponse fail() {
        return create()
                .setFlag(false)
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setErrMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    public static JsonResponse fail(String message) {
        return create()
                .setFlag(false)
                .setCode(HttpStatus.BAD_REQUEST.value())
                .setErrMessage(message);
    }
}
