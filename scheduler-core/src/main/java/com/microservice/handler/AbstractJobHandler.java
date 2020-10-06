package com.microservice.handler;

import com.microservice.dto.ReturnT;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zhangwei
 * @date 2020-09-20
 */
public abstract class AbstractJobHandler {
    public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
    public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
    public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);

    public abstract ReturnT<String> execute(String param) throws Exception;

    public void init() throws InvocationTargetException, IllegalAccessException {
    }

    public void destroy() throws InvocationTargetException, IllegalAccessException {
    }
}
