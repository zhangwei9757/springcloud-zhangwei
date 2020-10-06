package com.microservice.protocol;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.ExecutorJobMessage;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.scheduler.ExecutorGroupServerHandler;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 * 注册
 */
@Component
@Slf4j
public class RegisterRequest extends BaseProtocol {

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        String content = messageProtocol.getContent();
        ExecutorJobMessage jobMessage = JsonUtils.fromJson(content, ExecutorJobMessage.class);
        log.info(">>> 服务器接收到注册事件, 内容: {}", jobMessage);

        // TODO 写表，注册执行器: 服务名，注册时间，执行策略[默认轮巡]，注册序号[时间戳]
        long currentTimeMillis = System.currentTimeMillis();
        assert jobMessage != null;
        SchedulerConfigurationProperties properties = jobMessage.getProperties();
        String registerName = properties.getRegisterName();
        registerName += ("_" + currentTimeMillis);

        ExecutorGroupServerHandler.ALL_CHANNELS.put(registerName, channel);
        ExecutorGroupServerHandler.CHANNEL_GROUP.add(channel);
    }
}
