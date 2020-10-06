package com.microservice.exception;

import org.springframework.http.HttpStatus;

/**
 * @author zhangwei
 * @date 2020-06-09
 * <p>
 */
public class SchedulerCoreException extends RuntimeException {

    public SchedulerCoreException() {
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.msg = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    }

    public SchedulerCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulerCoreException(int code, String msg) {
        if (code <= 0) {
            this.code = HttpStatus.BAD_REQUEST.value();
        }
        this.msg = msg;
    }

    public SchedulerCoreException(String msg) {
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.msg = msg;
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
