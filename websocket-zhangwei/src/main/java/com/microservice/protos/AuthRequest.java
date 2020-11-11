package com.microservice.protos;

import com.microservice.websocket.BaseProtocol;
import com.microservice.websocket.WebSocketUser;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

/**
 * @author zhangwei
 * @date 2020-08-22
 * <p> 用户认证请求
 */
@Component
public class AuthRequest extends BaseProtocol {

    public String type;
    /**
     * 认证 Token
     */
    public String accessToken;

    class Return extends BaseProtocol {
        public String type;
        public String result;
    }

    @Override
    public void onProcess(WebSocketUser user, ByteBuffer buffer) {
        Return rci = new Return();
        rci.type = this.getProtoType();
        rci.result = LocalDateTime.now().toString() + ":::" + accessToken;
        user.send(rci);
    }
}
