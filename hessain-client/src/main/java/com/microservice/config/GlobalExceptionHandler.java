package com.microservice.config;

import com.microservice.beans.HttpStatusMicro;
import com.microservice.beans.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zw
 * @date 2019-10-09
 * <p>
 * 全局异常统一处理，"普通请求" && "Ajax请求"
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Object defaultExceptionHandle(Exception e, HttpServletRequest request) {

        final String securityException = "AuthenticationCredentialsNotFoundException";
        Class<? extends Throwable> eClass = e.getClass();
        String eClassName = eClass.getName();
        if (eClassName.contains(securityException)) {
            return JsonResult.fail(HttpStatusMicro.TOKEN_INVALIDATE);
        }
        log.error("--->>> 全局异常统一处理, 原因: {}", e.getLocalizedMessage());
        return JsonResult.fail(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
    }
}
