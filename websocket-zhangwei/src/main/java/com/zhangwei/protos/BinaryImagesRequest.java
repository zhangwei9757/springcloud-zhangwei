package com.zhangwei.protos;

import com.zhangwei.websocket.BaseProtocol;
import com.zhangwei.websocket.WebSocketUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author zhangwei
 * @date 2020-08-22
 * <p> 图片处理, 图片类型 .jpg
 */
@Component
@Slf4j
public class BinaryImagesRequest extends BaseProtocol {

    class Return extends BaseProtocol {
        public String type;
        public String result;
    }

    @Override
    public int getBinaryProtocol() {
        return 2;
    }

    @Override
    public void onProcess(WebSocketUser user, ByteBuffer buffer) {
        // TODO 图片保存路径，业务数据获取
        try (FileChannel fileChannel = FileChannel.open(Paths.get("F:\\wocketUpload.jpg"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            fileChannel.write(buffer);
            buffer.clear();
            log.info(">>> 发送者: {}, businessId: {}, 文件类型： {}", user.getUid(), user.getBusinessId(), ".jpg");
        } catch (Exception e) {
            log.error("receive protocol [" + this.getProtoType() + "] data(" + buffer + ") handler error:", e);
        }
    }
}
