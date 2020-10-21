package com.microservice.utils;

import org.springframework.http.HttpStatus;

/**
 * @author zw
 * @date 2020-10-16
 * <p> 错误码
 */
public class ErrCode {
    public static final String SUCCESS = "操作成功";
    public static final String FAIL = "操作失败";
    public static final String REGISTER_FAIL = "注册失败";
    public static final String DEREGISTER_FAIL = "注销失败";
    public static final String PARAMS_EMPTY = "参数为空";
    public static final String PARAMS_WRONGFUL = "参数不合法";
    public static final String ON_LINE = "在线中";
    public static final String OFF_LINE = "离线中";
    public static final String NOT_FOUND_RESULT = "未匹配到数据";
    public static final String INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
}
