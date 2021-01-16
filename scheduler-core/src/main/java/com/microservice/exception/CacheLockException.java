package com.microservice.exception;

/**
 * @author zhangwei
 * @date 2020-02-16
 * <p>
 * redis 分布式锁自定义异常
 */
public class CacheLockException extends RuntimeException {

    private String message;

    public CacheLockException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
