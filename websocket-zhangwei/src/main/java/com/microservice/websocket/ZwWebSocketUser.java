package com.microservice.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

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
public class ZwWebSocketUser extends WebSocketUser {

    public ZwWebSocketUser() {
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
     * ChannelHandler回调 会话生成时，迅速返回一个16bit的随机数用于会话通信等.
     */
    @Override
    public void onAdd() {
        // TODO 此处写对应已连接用户，上线后的回调事件
    }

    /**
     * ChannelHandler回调 会话结束时，如果会话认证通过，则有玩家信息，需要将玩家信息也退出
     * <p>
     * 这个函数只会由网络层回调，所以他是线程安全的
     */
    @Override
    public void onDelete() {
        // TODO 此处写对应准备离线用户，下线前的回调事件
    }
}
