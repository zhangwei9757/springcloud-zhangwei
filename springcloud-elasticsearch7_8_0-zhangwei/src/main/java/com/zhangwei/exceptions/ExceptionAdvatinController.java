package com.zhangwei.exceptions;

import com.zhangwei.dto.ResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zhangwei
 * @date 2020-06-29
 * <p>
 */
@RestControllerAdvice
public class ExceptionAdvatinController {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseDto errorHandler(Exception ex) {
        return ResponseDto.error(ex.getMessage());
    }
}
