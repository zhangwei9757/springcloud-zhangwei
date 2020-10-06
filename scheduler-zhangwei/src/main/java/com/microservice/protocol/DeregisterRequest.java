package com.microservice.protocol;

import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.scheduler.ExecutorGroupServerHandler;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 * 注销
 */
@Component
@Slf4j
public class DeregisterRequest extends BaseProtocol {

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        // TODO 当集群与客户端断开连接时，需要写表记录已下线 [客户端下线]
        log.info(">>> 服务器接收到注销事件, 内容: {}", channel);

        String name = ExecutorGroupServerHandler.findName(channel);
        ExecutorGroupServerHandler.ALL_CHANNELS.remove(name);
        ExecutorGroupServerHandler.CHANNEL_GROUP.remove(channel);
    }
}
