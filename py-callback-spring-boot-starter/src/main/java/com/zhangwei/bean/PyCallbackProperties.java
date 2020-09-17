package com.zhangwei.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-29-14
 * <p>
 * py 回调默认API
 */
@ConfigurationProperties(prefix = "microservice.py")
@ConditionalOnProperty(prefix = "microservice.py", value = "enabled", havingValue = "true")
public class PyCallbackProperties {

    private String callbackUrl = "http://localhost:8080/callback/handle";

    public PyCallbackProperties() {
    }

    public PyCallbackProperties(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PyCallbackProperties that = (PyCallbackProperties) object;
        return Objects.equals(callbackUrl, that.callbackUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callbackUrl);
    }
}
