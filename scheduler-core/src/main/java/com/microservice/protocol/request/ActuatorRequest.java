package com.microservice.protocol.request;

import com.microservice.bean.SchedulerTask;
import com.microservice.dto.ReturnT;
import com.microservice.handler.AbstractJobHandler;
import com.microservice.handler.DefaultExecutorJobHolder;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.protocol.BaseProtocol;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 * 执行器调度
 */
@Component
@Slf4j
public class ActuatorRequest extends BaseProtocol {

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        log.info(">>> 客户端接收到执行器调度事件, 内容: {}", channel);
        String content = messageProtocol.getContent();
        SchedulerTask task = JsonUtils.fromJson(content, SchedulerTask.class);
        assert task != null;
        String actuatorKey = task.getActuatorKey();
        AbstractJobHandler jobHandler = DefaultExecutorJobHolder.loadExecutorJobHandler(actuatorKey);

        try {
            ReturnT<String> executeResult = jobHandler.execute(content);
            // TODO 此处要处理执行日志

            // TODO 此处要回写处理结果
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
