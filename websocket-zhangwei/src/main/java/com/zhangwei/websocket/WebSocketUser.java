package com.zhangwei.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhangwei.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Collection;

/**
 * @author zhangwei
 * @date 2020-08-22
 * <p> 带有会话的一个用户
 */
@Component
@Scope("prototype")
@Slf4j
public class WebSocketUser {

    public WebSocketUser() {
    }

    /**
     * 会话名
     */
    protected String uid;
    protected int businessId;

    /**
     * 对应的处理器上下文
     */
    protected WebSocketSession socketSession;

    private InetSocketAddress address;

    private final Object sendMonitor = new Object();

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

    /**
     * remote ip address
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public WebSocketSession getSocketSession() {
        return socketSession;
    }

    public void setSocketSession(WebSocketSession socketSession) {
        this.socketSession = socketSession;
    }

    /**
     * authenticate session
     *
     * @param session
     * @throws Exception
     */
    public void authenticate(WebSocketSession session) throws Exception {
        this.socketSession = session;
        this.setAddress(session.getRemoteAddress());

        Principal p = socketSession.getPrincipal();
        if (p != null) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) p;
            Collection<GrantedAuthority> cs = token.getAuthorities();

        } else {
            throw new Exception("authentication fail");
        }
    }

    /**
     * 服务器主动关闭这个Session
     */
    public void close() {
        synchronized (sendMonitor) {
            if (socketSession != null) {
                try {
                    if (socketSession.isOpen()) {
                        socketSession.close();
                    }
                } catch (Exception ex) {
                    log.error("close session error:" + ex.getMessage());
                } finally {
                    socketSession = null;
                }
            }
        }
    }

    /**
     * ChannelHandler回调 会话生成时，迅速返回一个16bit的随机数用于会话通信等.
     */
    public void onAdd() {
        // TODO 此处写对应已连接用户，上线后的回调事件
    }

    /**
     * ChannelHandler回调 会话结束时，如果会话认证通过，则有玩家信息，需要将玩家信息也退出
     * <p>
     * 这个函数只会由网络层回调，所以他是线程安全的
     */
    public void onDelete() {
        // TODO 此处写对应准备离线用户，下线前的回调事件
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
