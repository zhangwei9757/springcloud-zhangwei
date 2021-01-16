package com.microservice.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.dto.FlaskCallBackDto;

/**
 * @author zhangwei
 * @date 2020-09-14
 * <p> 自定义回调协议处理方案
 */
public class BaseProtocol {

    public BaseProtocol() {
    }

    /**
     * 预处理
     *
     * @param callBackDto
     * @param args
     */
    protected void preProcess(FlaskCallBackDto callBackDto, Object... args) {
    }

    /**
     * 后处理
     *
     * @param callBackDto
     * @param args
     */
    protected void postProcess(FlaskCallBackDto callBackDto, Object... args) {
    }

    /**
     * 协议接收后的处理逻辑
     */
    public void process(FlaskCallBackDto callBackDto, Object... args) {
        preProcess(callBackDto, args);
        onProcess(callBackDto, args);
        postProcess(callBackDto, args);
    }

    public void onProcess(FlaskCallBackDto callBackDto, Object... args) {
    }

    @JsonIgnore
    public String getProtoType() {
        return getClass().getSimpleName();
    }
}
