package com.zhangwei.websocket;

import com.zhangwei.utils.ByteUtils;
import com.zhangwei.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author zhangwei
 * @date 2020-08-17
 * <p> 服务器代表
 */
@Service
@Slf4j
public class WebSocketServer {

    @Autowired
    private ApplicationContext applicationContext;

    private final static String USER_ATTR = "carUser";

    /**
     * 用户 与Session的映射
     */
    private HashMap<String, WebSocketUser> usersSessionMap = new HashMap<>();

    private final Object users_monitor = new Object();

    /**
     * key为协议， value是时间
     */
    private HashMap<String, Long> protoTime = new HashMap<>();

    /**
     * 添加用户
     *
     * @param session
     */
    public void onAddSession(WebSocketSession session) {
        WebSocketUser user = applicationContext.getBean(WebSocketUser.class);
        user.socketSession = session;
        // TODO uid 应该为token 或者其它类似 主键数据
        user.uid = session.getAttributes().getOrDefault("accessToken", "").toString();
        user.setAddress(session.getRemoteAddress());

        session.getAttributes().put(USER_ATTR, user);
        Object accessToken = user.uid;
        usersSessionMap.put(accessToken.toString(), user);

        log.info("-------------------- 当前在线用户列表：---------------------");
        usersSessionMap.keySet().forEach(log::info);
        log.info("---------------------------------------------------------");

        user.onAdd();
    }

    /**
     * 删除用户
     *
     * @param session
     */
    public void onDelSession(WebSocketSession session) {
        try {
            WebSocketUser user = (WebSocketUser) session.getAttributes().get(USER_ATTR);
            if (user != null) {
                removeSession(user);
            }
        } catch (Exception e) {
            log.error("onDelSession error:", e);
        }
    }


    /**
     * 实际的业务子类继承 确定删除的session是当前的会话
     *
     * @param user
     * @return
     */
    public void removeSession(WebSocketUser user) {
        boolean flag = false;
        synchronized (users_monitor) {
            WebSocketUser now = usersSessionMap.getOrDefault(user.getUid(), null);
            if (now == user) {
                usersSessionMap.remove(user.getUid());
                flag = true;
            }
        }

        if (flag) {
            user.onDelete();
        }
    }

    /**
     * 消息处理 [文本]
     *
     * @param session
     * @param type
     * @param data
     */
    public void onMessage(WebSocketSession session, String type, String data) {
        WebSocketUser user = (WebSocketUser) session.getAttributes().get(USER_ATTR);

        log.info(">>> 发送者: {}, 协议类型: {}, 内容： {}", user.uid, type, data);

        try {
            process(type, data, user);
        } catch (Exception e) {
            log.error("receive protocol [" + type + "] data(" + data + ") handler error:", e);
        }
    }

    /**
     * 消息处理 [二进制]
     * 4    |   1    |   N
     * 逻辑主键   二进制处理类型  二进制数据
     * <p>
     * 二进制处理类型:
     * 1: 文本
     * 2: jpg
     * 3: png
     * 4: avi
     * 5: ...
     *
     * @param session
     * @param buffer
     */
    public void onMessage(WebSocketSession session, ByteBuffer buffer) {
        WebSocketUser user = (WebSocketUser) session.getAttributes().get(USER_ATTR);

        byte[] array = buffer.array();
        byte[] businessId = Arrays.copyOf(array, 4);
        byte[] dataType = Arrays.copyOfRange(array, businessId.length, businessId.length + 1);
        byte[] data = Arrays.copyOfRange(array, businessId.length + dataType.length, array.length);

        int businessIdInt = ByteUtils.byteArrayToInt(businessId, 0);
        int dataTypeInt = ByteUtils.byte2int(dataType);

        user.setBusinessId(businessIdInt);
        BaseProtocol protocol = this.getProtoClass(dataTypeInt);

        if (null != protocol) {
            protocol.onProcess(user, ByteBuffer.wrap(data));
        }
    }

    /**
     * 错误处理
     *
     * @param session
     * @param ex
     */
    public void onError(WebSocketSession session, String ex) {
        WebSocketUser webSocketUser = (WebSocketUser) session.getAttributes().get(USER_ATTR);
        if (webSocketUser != null) {
            webSocketUser.close();
        }
    }

    /**
     * 协议解析
     *
     * @param type
     * @param data
     * @param user
     * @throws Exception
     */
    public void process(String type, String data, WebSocketUser user) throws Exception {
        Class<? extends BaseProtocol> bp = this.getProtoClass(type);
        if (bp == null) {
            log.error("协议名 (" + type + ") 对应的处理组件不存在.");

            // 尽量提取发送来的数据中的type字段，返回给客户端
            NotifyError errorInfo = new NotifyError();
            errorInfo.type = type;
            errorInfo.result = "命令(" + type + ")没有对应的处理逻辑";
            user.send(errorInfo);
            return;
        }

        BaseProtocol protocol = JsonUtils.unmarshal(data, bp);
        if (protocol != null) {
            protocol.process(user, null);
        }
    }

    /**
     * 根据传入的名字查询协议的具体类型，这个函数的作用是得到json反序列化需要的类型信息
     *
     * @param type
     * @return
     */
    public Class<? extends BaseProtocol> getProtoClass(String type) {
        try {
            byte[] items = type.getBytes();

            // springboot 默认注册的实体协议第一个字母是小写
            int i = (int) items[0];
            if (i < 97) {
                i = i - 65 + 97;
                items[0] = (byte) i;
                type = new String(items);
            }
            return (Class<? extends BaseProtocol>) applicationContext.getType(type);
        } catch (NoSuchBeanDefinitionException ex) {
            log.error("--- 没有找到协议:" + type + " 失败:" + ex.getMessage());
        }
        return null;
    }

    /**
     * 根据传入的数字查询协议的具体类型，这个函数的作用是得到指定类型二进制请求处理
     *
     * @param type
     * @return
     */
    public BaseProtocol getProtoClass(int type) {
        try {
            Map<String, BaseProtocol> beansOfType = applicationContext.getBeansOfType(BaseProtocol.class);
            if (!CollectionUtils.isEmpty(beansOfType)) {
                Optional<BaseProtocol> protocol = beansOfType.values().stream().filter(f -> f.getBinaryProtocol() == type).findFirst();
                return protocol.orElse(null);
            }
            return null;
        } catch (NoSuchBeanDefinitionException ex) {
            log.error("--- 没有找到协议:" + type + " 失败:" + ex.getMessage());
        }
        return null;
    }


    /**
     * 判断协议，与协议之间的间隔 [用于限制当前请求过于频繁]
     *
     * @param proto
     * @param interval
     * @return
     */
    public boolean judgeProtocolInterval(BaseProtocol proto, int interval) {
        long now = System.currentTimeMillis() / 1000;
        if (interval > 0) {
            long then = protoTime.getOrDefault(proto.getProtoType(), 0L);
            // 两次发送协议的间隔太短
            if (now - then < interval) {
                return false;
            }
        }
        protoTime.put(proto.getProtoType(), now);
        return true;
    }

    /***
     * 服务器主动发送给指定的玩家
     * @param proto
     * @return
     */
    public boolean send(String id, BaseProtocol proto) {
        WebSocketUser user = find(id);

        if (user != null) {
            user.send(proto);
            return true;
        }
        return false;
    }

    /**
     * 查询用户
     * @param uid
     * @return
     */
    public WebSocketUser find(String uid) {
        WebSocketUser user;

        synchronized (users_monitor) {
            user = usersSessionMap.getOrDefault(uid, null);
        }
        return user;
    }

    /**
     * 广播消息
     *
     * @param proto
     */
    public void broadcast(BaseProtocol proto) {

        List<WebSocketUser> tmp;
        synchronized (users_monitor) {
            tmp = new ArrayList<>(usersSessionMap.values());
        }

        tmp.forEach((WebSocketUser webSocketUser) -> webSocketUser.send(proto));
    }

    /**
     * 通知指定的玩家, 相同的内容
     *
     * @param proto
     * @param users
     */
    public void broadcast(BaseProtocol proto, List<String> users) {
        try {
            final String data = JsonUtils.marshal(proto);

            if (data != null) {
                users.forEach((u) -> {
                    WebSocketUser su = find(u);
                    if (su != null) {
                        su.send(proto.getProtoType(), data);
                    }
                });
            }
        } catch (Exception e) {
            log.error("WebSocketServer broadcast error:", e);
        }
    }
}
