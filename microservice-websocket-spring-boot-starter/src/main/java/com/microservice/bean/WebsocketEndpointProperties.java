package com.microservice.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-08-26
 * <p>
 * Spring Websocket 端点/Js端点，参数配置默认值
 */
@ConfigurationProperties(prefix = "microservice.websocket")
public class WebsocketEndpointProperties {

    private String websocketEndpoint = "socket";
    private String[] websocketEndpointAllowedOrigins = {"*"};

    private String websocketJsEndpoint = "socketJs";
    private String[] websocketJsEndpointAllowedOrigins = {"*"};

    /**
     * 3000L
     */
    private long asyncSendTimeout = 0L;
    /**
     * 1800000L
     */
    private long maxSessionIdleTimeout = 0L;
    /**
     * 1024 * 1024
     */
    private int maxTextMessageBufferSize = 1024 * 1024;
    /**
     * 1024 * 1024 * 20
     */
    private int maxBinaryMessageBufferSize = 1024 * 1024 * 20;

    public long getAsyncSendTimeout() {
        return asyncSendTimeout;
    }

    public void setAsyncSendTimeout(long asyncSendTimeout) {
        this.asyncSendTimeout = asyncSendTimeout;
    }

    public long getMaxSessionIdleTimeout() {
        return maxSessionIdleTimeout;
    }

    public void setMaxSessionIdleTimeout(long maxSessionIdleTimeout) {
        this.maxSessionIdleTimeout = maxSessionIdleTimeout;
    }

    public String getWebsocketEndpoint() {
        return websocketEndpoint;
    }

    public void setWebsocketEndpoint(String websocketEndpoint) {
        this.websocketEndpoint = websocketEndpoint;
    }

    public String[] getWebsocketEndpointAllowedOrigins() {
        return websocketEndpointAllowedOrigins;
    }

    public void setWebsocketEndpointAllowedOrigins(String[] websocketEndpointAllowedOrigins) {
        this.websocketEndpointAllowedOrigins = websocketEndpointAllowedOrigins;
    }

    public String getWebsocketJsEndpoint() {
        return websocketJsEndpoint;
    }

    public void setWebsocketJsEndpoint(String websocketJsEndpoint) {
        this.websocketJsEndpoint = websocketJsEndpoint;
    }

    public String[] getWebsocketJsEndpointAllowedOrigins() {
        return websocketJsEndpointAllowedOrigins;
    }

    public void setWebsocketJsEndpointAllowedOrigins(String[] websocketJsEndpointAllowedOrigins) {
        this.websocketJsEndpointAllowedOrigins = websocketJsEndpointAllowedOrigins;
    }

    public int getMaxTextMessageBufferSize() {
        return maxTextMessageBufferSize;
    }

    public void setMaxTextMessageBufferSize(int maxTextMessageBufferSize) {
        this.maxTextMessageBufferSize = maxTextMessageBufferSize;
    }

    public int getMaxBinaryMessageBufferSize() {
        return maxBinaryMessageBufferSize;
    }

    public void setMaxBinaryMessageBufferSize(int maxBinaryMessageBufferSize) {
        this.maxBinaryMessageBufferSize = maxBinaryMessageBufferSize;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        WebsocketEndpointProperties that = (WebsocketEndpointProperties) object;
        return asyncSendTimeout == that.asyncSendTimeout &&
                maxSessionIdleTimeout == that.maxSessionIdleTimeout &&
                maxTextMessageBufferSize == that.maxTextMessageBufferSize &&
                maxBinaryMessageBufferSize == that.maxBinaryMessageBufferSize &&
                Objects.equals(websocketEndpoint, that.websocketEndpoint) &&
                Arrays.equals(websocketEndpointAllowedOrigins, that.websocketEndpointAllowedOrigins) &&
                Objects.equals(websocketJsEndpoint, that.websocketJsEndpoint) &&
                Arrays.equals(websocketJsEndpointAllowedOrigins, that.websocketJsEndpointAllowedOrigins);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(websocketEndpoint, websocketJsEndpoint, asyncSendTimeout, maxSessionIdleTimeout, maxTextMessageBufferSize, maxBinaryMessageBufferSize);
        result = 31 * result + Arrays.hashCode(websocketEndpointAllowedOrigins);
        result = 31 * result + Arrays.hashCode(websocketJsEndpointAllowedOrigins);
        return result;
    }

    @Override
    public String toString() {
        return "WebsocketEndpointProperties{" +
                "websocketEndpoint='" + websocketEndpoint + '\'' +
                ", websocketEndpointAllowedOrigins=" + Arrays.toString(websocketEndpointAllowedOrigins) +
                ", websocketJsEndpoint='" + websocketJsEndpoint + '\'' +
                ", websocketJsEndpointAllowedOrigins=" + Arrays.toString(websocketJsEndpointAllowedOrigins) +
                ", asyncSendTimeout=" + asyncSendTimeout +
                ", maxSessionIdleTimeout=" + maxSessionIdleTimeout +
                ", maxTextMessageBufferSize=" + maxTextMessageBufferSize +
                ", maxBinaryMessageBufferSize=" + maxBinaryMessageBufferSize +
                '}';
    }
}
