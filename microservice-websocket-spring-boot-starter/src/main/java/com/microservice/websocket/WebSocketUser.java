package com.microservice.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * @author zhangwei
 * @date 2020-08-22
 * <p> 带有会话的一个用户
 */
@Slf4j
public class WebSocketUser implements Closeable {

    public WebSocketUser() {
    }

    /**
     * 会话名
     */
    protected String uid;

    /**
     * 业务逻辑主键
     */
    protected int businessId;

    /**
     * 对应的处理器上下文
     */
    protected WebSocketSession socketSession;

    protected InetSocketAddress address;

    protected final Object sendMonitor = new Object();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public WebSocketSession getSocketSession() {
        return socketSession;
    }

    public void setSocketSession(WebSocketSession socketSession) {
        this.socketSession = socketSession;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Object getSendMonitor() {
        return sendMonitor;
    }

    public void onAdd() {

    }

    public void onDelete() {

    }

    /**
     * The server actively shut down the session
     */
    @Override
    public void close() {
        synchronized (sendMonitor) {
            if (socketSession != null) {
                try {
                    if (socketSession.isOpen()) {
                        socketSession.close();
                    }
                } catch (Exception ex) {
                    log.error(">>> close session error:", ex);
                } finally {
                    socketSession = null;
                }
            }
        }
    }

    /**
     * 用户发送协议，发送给指定另一个用户
     *
     * @param proto
     */
    public boolean send(BaseProtocol proto) {
        try {
            String data = JsonUtils.marshal(proto);
            if (socketSession == null) {
                return false;
            }
            synchronized (sendMonitor) {
                socketSession.sendMessage(new TextMessage(proto.getProtoType() + "|" + data));
            }
        } catch (JsonProcessingException e) {
            log.error("send protocol JsonProcessingException:" + e.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("send protocol error:" + ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 发送协议 重载
     *
     * @param type
     * @param data
     * @return
     */
    public boolean send(String type, String data) {
        try {
            if (socketSession == null) {
                return false;
            }

            synchronized (sendMonitor) {
                socketSession.sendMessage(new TextMessage(type + "|" + data));
            }
        } catch (Exception ex) {
            log.error("send protocol error:", ex);
            return false;
        }
        return true;
    }
}
