package com.microservice.protocol;

import com.microservice.client.BaseProtocol;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.service.ISchedulerTaskCronService;
import com.microservice.service.ISchedulerTaskGivenService;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author zw
 * @date 2020-10-19
 * <p>
 * 注册cron ,given 任务
 */
@Component
@Slf4j
public class RegistryGivenTaskRequest extends BaseProtocol {

    @Autowired
    private ISchedulerTaskCronService taskCronService;

    @Autowired
    private ISchedulerTaskGivenService taskGivenService;

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        // 调度自动执行
        autRegistryTask(messageProtocol, channel);
    }

    /**
     * 调度自动执行
     *
     * @param messageProtocol
     * @param channel
     */
    @Transactional(rollbackFor = Exception.class)
    public void autRegistryTask(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {

        String content = messageProtocol.getContent();

        SchedulerTaskGivenRequestDto givenTask = JsonUtils.fromJson(content, SchedulerTaskGivenRequestDto.class);

        if (Objects.nonNull(givenTask)) {
            // 表示注册指定时间的任务
            boolean b = taskGivenService.registryGivenTask(givenTask);
            log.info(">>> 服务器接收到调度任务注册given事件, 内容: {}, 注册结果: {}", givenTask, b);
        }
    }
}
