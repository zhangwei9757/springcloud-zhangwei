package com.microservice.exception;

import com.microservice.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p>
 */
@RestControllerAdvice
@Slf4j
@Controller
public class RestControllerAdviceHandler implements ErrorController {

    @ExceptionHandler(value = Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("url:{}, msg:{}", request.getRequestURL(), e.getLocalizedMessage(), e);
        return ResponseDto.error()
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMsg(e.getLocalizedMessage());
    }

    @ExceptionHandler(value = SchedulerCoreException.class)
    public Object handleTpvdbException(SchedulerCoreException e, HttpServletRequest request) {
        log.error("url:{}, msg:{}", request.getRequestURL(), e.getLocalizedMessage(), e);
        return ResponseDto.error()
                .setCode(e.getCode())
                .setMsg(e.getMsg());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = {"/error"})
    public Object error(HttpServletRequest request) {
        return ResponseDto.error().setCode(HttpStatus.NOT_FOUND.value())
                .setMsg(HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}