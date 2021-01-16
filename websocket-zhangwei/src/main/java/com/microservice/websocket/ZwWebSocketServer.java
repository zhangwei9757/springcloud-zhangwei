package com.microservice.websocket;

import com.microservice.utils.ByteUtils;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author zhangwei
 * @date 2020-08-17
 * <p> 服务器代表
 */
@Service
@Slf4j
public class ZwWebSocketServer extends WebSocketServer {

    @Autowired
    private ApplicationContext applicationContext;

    private final static String USER_ATTR = "carUser";

    /**
     * 添加用户
     *
     * @param session
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void onMessage(WebSocketSession session, ByteBuffer buffer) {
        WebSocketUser user = (WebSocketUser) session.getAttributes().get(USER_ATTR);

        byte[] array = buffer.array();
        byte[] businessId = Arrays.copyOf(array, 4);
        byte[] dataType = Arrays.copyOfRange(array, businessId.length, businessId.length + 1);
        byte[] data = Arrays.copyOfRange(array, businessId.length + dataType.length, array.length);

        int businessIdInt = ByteUtils.byteArrayToInt(businessId, 0);
        int dataTypeInt = ByteUtils.byte2int(dataType);

        user.setBusinessId(businessIdInt);
        BaseProtocol protocol = this.getProtoClass(dataTypeInt, applicationContext);

        if (null != protocol) {
            protocol.onProcess(user, ByteBuffer.wrap(data));
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
        Class<? extends BaseProtocol> bp = this.getProtoClass(type, applicationContext);
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
}
