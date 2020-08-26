package com.zhangwei.websocket;

import com.zhangwei.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author zhangwei
 * @date 2020-08-17
 * <p> 服务器代表
 */
@Slf4j
public class WebSocketServer {

    /**
     * session attr key
     */
    protected final static String USER_ATTR = "scUser";

    /**
     * user mapping Session
     */
    protected HashMap<String, WebSocketUser> usersSessionMap = new HashMap<>();

    /**
     * object locked
     */
    protected final Object users_monitor = new Object();

    /**
     * protocol Interval time, [key:protocol] [value:timestamp]
     */
    protected HashMap<String, Long> protoTime = new HashMap<>();

    /**
     * add session
     *
     * @param session
     */
    protected void onAddSession(WebSocketSession session) {
    }

    /**
     * delete session
     *
     * @param session
     */
    protected void onDelSession(WebSocketSession session) {
    }

    /**
     * textMessage handling
     *
     * @param session
     * @param type
     * @param data
     */
    protected void onMessage(WebSocketSession session, String type, String data) {
    }

    /**
     * binaryMessage handling
     * 4         |      1     |   N
     * bussionId   binaryType  binaryData
     * <p>
     * binary handle type:
     * 1: text
     * 2: jpg
     * 3: png
     * 4: avi
     * 5: ...
     *
     * @param session
     * @param buffer
     */
    protected void onMessage(WebSocketSession session, ByteBuffer buffer) {
    }

    /**
     * error handling
     *
     * @param session
     * @param ex
     */
    protected void onError(WebSocketSession session, String ex) {
        WebSocketUser webSocketUser = (WebSocketUser) session.getAttributes().get(USER_ATTR);
        if (webSocketUser != null) {
            webSocketUser.close();
            log.info(">>> onError reason: {}", ex);
        }
    }

    /**
     * Judge the protocol, and the interval between the protocol,
     * used to limit the current request is too frequent
     *
     * @param proto
     * @param interval
     * @return
     */
    protected boolean judgeProtocolInterval(BaseProtocol proto, int interval) {
        long now = System.currentTimeMillis() / 1000;
        if (interval > 0) {
            long then = protoTime.getOrDefault(proto.getProtoType(), 0L);
            // The interval between two sending protocols is too short
            if (now - then < interval) {
                return false;
            }
        }
        protoTime.put(proto.getProtoType(), now);
        return true;
    }

    /***
     * Server pushes messages actively
     * @param proto
     * @return
     */
    protected boolean send(String id, BaseProtocol proto) {
        WebSocketUser user = find(id);

        if (user != null) {
            user.send(proto);
            return true;
        }
        return false;
    }

    /**
     * from mapping find session
     *
     * @param uid
     * @return
     */
    protected WebSocketUser find(String uid) {
        WebSocketUser user;

        synchronized (users_monitor) {
            user = usersSessionMap.getOrDefault(uid, null);
        }
        return user;
    }

    /**
     * broadcast message
     *
     * @param proto
     */
    protected void broadcast(BaseProtocol proto) {
        List<WebSocketUser> tmp;

        synchronized (users_monitor) {
            tmp = new ArrayList<>(usersSessionMap.values());
        }

        tmp.forEach((WebSocketUser webSocketUser) -> webSocketUser.send(proto));
    }

    /**
     * broadcast messages
     *
     * @param proto
     * @param users
     */
    protected void broadcast(BaseProtocol proto, List<String> users) {
        try {
            final String data = JsonUtils.marshal(proto);

            assert null == data : ">>> webSocketServer broadcast messages is null or empty";

            users.forEach((u) -> {
                WebSocketUser su = find(u);
                if (null != su) {
                    su.send(proto.getProtoType(), data);
                }
            });
        } catch (Exception e) {
            log.error(">>> webSocketServer broadcast messages error:", e);
        }
    }

    /**
     * 根据传入的名字查询协议的具体类型，这个函数的作用是得到json反序列化需要的类型信息
     *
     * @param type
     * @return
     */
    public Class<? extends BaseProtocol> getProtoClass(String type, ApplicationContext applicationContext) {
        try {
            byte[] items = type.getBytes();

            int i = (int) items[0];
            if (i < 97) {
                i = i - 65 + 97;
                items[0] = (byte) i;
                type = new String(items);
            }
            return (Class<? extends BaseProtocol>) applicationContext.getType(type);
        } catch (NoSuchBeanDefinitionException ex) {
            log.error(">>> No agreement found: {}, error:", type, ex);
        }
        return null;
    }

    /**
     * 根据传入的数字查询协议的具体类型，这个函数的作用是得到指定类型二进制请求处理
     *
     * @param type
     * @return
     */
    public BaseProtocol getProtoClass(int type, ApplicationContext applicationContext) {
        try {
            Map<String, BaseProtocol> beansOfType = applicationContext.getBeansOfType(BaseProtocol.class);
            if (!CollectionUtils.isEmpty(beansOfType)) {
                Optional<BaseProtocol> protocol = beansOfType.values().stream().filter(f -> f.getBinaryProtocol() == type).findFirst();
                return protocol.orElse(null);
            }
            return null;
        } catch (NoSuchBeanDefinitionException ex) {
            log.error(">>> No agreement found: {}, error:", type, ex);
        }
        return null;
    }
}
