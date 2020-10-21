package com.microservice.protocol;

import com.microservice.bean.SchedulerTaskMessage;
import com.microservice.client.BaseProtocol;
import com.microservice.dto.ReturnT;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.enums.ProtocolStausEnum;
import com.microservice.handler.AbstractJobHandler;
import com.microservice.handler.DefaultExecutorJobHolder;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.PingPongUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 * 执行器调度
 */
@Component
@Slf4j
public class ActuatorRequest extends BaseProtocol {

    @Autowired
    private Environment environment;

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        // 调度自动执行
        autoExecute(messageProtocol, channel);
    }

    /**
     * 调度自动执行
     *
     * @param messageProtocol
     * @param channel
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoExecute(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        log.info(">>> 客户端接收到执行器调度事件, 内容: {}", channel);
        String content = messageProtocol.getContent();
        SchedulerTaskMessage task = JsonUtils.fromJson(content, SchedulerTaskMessage.class);
        assert task != null;
        String jobHandler = task.getJobHandler();
        String jobHandlerParam = task.getJobHandlerParam();
        AbstractJobHandler jobMethod = DefaultExecutorJobHolder.loadExecutorJobHandler(jobHandler);

        String address = RedisDefaultClientHandler.findAddressByChannel(channel);
        String port = environment.getProperty("server.port");
        task.setExecutorAddress(address + ":" + port);

        try {
            boolean success = false;
            String triggerMsg = null;
            LocalDateTime triggerTime = LocalDateTime.now();
            int triggerCode = HttpStatus.OK.value();
            ReturnT<String> executeResult = null;
            try {
                executeResult = jobMethod.execute(jobHandlerParam);
                success = true;
            } catch (Exception e) {
                success = false;
                triggerCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
                triggerMsg = e.getLocalizedMessage();
            }

            log.info(">>> ActuatorRequest: {}", executeResult);

            // 执行没有异常, 直接使用结果赋值, 出现错误使用异常捕获赋值
            int code = success ? executeResult.getCode() : HttpStatus.INTERNAL_SERVER_ERROR.value();
            String msg = success ? executeResult.getMsg() : triggerMsg;
            String content1 = executeResult.getContent();

            boolean cron = task.isCron();
            if (cron) {
                SchedulerTaskCronRequestDto taskGivenDto = task.getTaskCronDto();
                taskGivenDto.setExecuteTime(LocalDateTime.now());
                taskGivenDto.setCode(code);
                taskGivenDto.setMsg(msg);
                taskGivenDto.setContent(content1);
                taskGivenDto.setExecuteTime(LocalDateTime.now());

                taskGivenDto.setTriggerTime(triggerTime);
                taskGivenDto.setTriggerCode(triggerCode);
                taskGivenDto.setTriggerMsg(triggerMsg);

                String json = JsonUtils.toJson(task);
                MessageProtocolPoJo.MessageProtocol callBackMessageProtocol = MessageProtocolPoJo.MessageProtocol
                        .newBuilder()
                        // 写死协议名即可，开发对应关系创建即注定了
                        .setProtocol(ProtocolStausEnum.callbackGivenStatusRequest.name())
                        .setLen(json.getBytes(PingPongUtils.CHARSET).length)
                        .setContent(json)
                        .build();
                channel.writeAndFlush(callBackMessageProtocol);

                boolean ok = Objects.deepEquals(code, HttpStatus.OK.value());
                log.info(">>> 客户端接收到执行器调度事件, 执行结果: {}, 回写内容: {}", ok, json);
            } else {
                SchedulerTaskGivenRequestDto taskGivenDto = task.getTaskGivenDto();
                taskGivenDto.setExecuteTime(LocalDateTime.now());
                taskGivenDto.setCode(code);
                taskGivenDto.setMsg(msg);
                taskGivenDto.setContent(content1);
                taskGivenDto.setExecuteTime(LocalDateTime.now());

                taskGivenDto.setTriggerTime(triggerTime);
                taskGivenDto.setTriggerCode(triggerCode);
                taskGivenDto.setTriggerMsg(triggerMsg);

                String json = JsonUtils.toJson(task);
                MessageProtocolPoJo.MessageProtocol callBackMessageProtocol = MessageProtocolPoJo.MessageProtocol
                        .newBuilder()
                        // 写死协议名即可，开发对应关系创建即注定了
                        .setProtocol(ProtocolStausEnum.callbackGivenStatusRequest.name())
                        .setLen(json.getBytes(PingPongUtils.CHARSET).length)
                        .setContent(json)
                        .build();
                channel.writeAndFlush(callBackMessageProtocol);

                boolean ok = Objects.deepEquals(code, HttpStatus.OK.value());
                log.info(">>> 客户端接收到执行器调度事件, 执行结果: {}, 回写内容: {}", ok, json);
            }
        } catch (Exception e) {
            log.info(">>> 客户端接收到执行器调度事件, 执行结果: exception, 错误原因: {}", e.getLocalizedMessage(), e);
        }
    }
}
