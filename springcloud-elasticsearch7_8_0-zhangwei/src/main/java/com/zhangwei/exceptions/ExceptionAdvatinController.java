package com.zhangwei.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2020-06-29
 * <p>
 */
@RestControllerAdvice
public class ExceptionAdvatinController {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {
        Map map = new HashMap(2);
        map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("msg", ex.getMessage());
        return map;
    }
}
