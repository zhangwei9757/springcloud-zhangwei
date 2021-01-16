package com.microservice.protos;

import com.google.common.base.Charsets;
import com.microservice.websocket.BaseProtocol;
import com.microservice.websocket.WebSocketUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author zhangwei
 * @date 2020-08-22
 * <p> 二进制文本
 */
@Component
@Slf4j
public class BinaryTextRequest extends BaseProtocol {

    class Return extends BaseProtocol {
        public String type;
        public String result;
    }

    @Override
    public int getBinaryProtocol() {
        return 1;
    }

    @Override
    public void onProcess(WebSocketUser user, ByteBuffer buffer) {
        Charset charset = Charset.forName(Charsets.UTF_8.name());
        CharsetDecoder decoder = charset.newDecoder();
        // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
        CharBuffer charBuffer = null;
        try {
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            String content = charBuffer.toString();

            log.info(">>> 接收到 " + user.getUid() + " 发送的二进制转字符串内容: " + content);
        } catch (Exception e) {
            log.error("receive protocol [" + this.getProtoType() + "] data(" + buffer + ") handler error:", e);
        }
    }
}
