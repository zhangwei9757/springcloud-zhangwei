package com.microservice.controller;

import com.microservice.client.ExecutorClient;
import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.server.TestRegistryDeRegistryTask;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@RestController
public class ClientController {

    @Autowired
    private ExecutorClient executorClient;

    @Autowired
    private TestRegistryDeRegistryTask registryTask;

    @GetMapping(value = "/test")
    @ResponseBody
    public ResponseDto test() {
        Channel channel = executorClient.getChannel();

        String test = "我是测试客户端发送给服务的消息";
        MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol
                .newBuilder()
                .setLen(test.getBytes(Charset.defaultCharset()).length)
                .setContent(test)
                .build();
        channel.writeAndFlush(messageProtocol);
        return ResponseDto.success(test);
    }

    @PostMapping(value = "/registryCronTask")
    @ResponseBody
    public ResponseDto registryCronTask(@RequestBody SchedulerTaskCronRequestDto cronRequestDto) {
        boolean registry = registryTask.registry(cronRequestDto);
        return registry ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/deRegistryCronTask")
    @ResponseBody
    public ResponseDto deRegistryCronTask(@RequestBody SchedulerTaskCronRequestDto cronRequestDto) {
        boolean registry = registryTask.deRegistry(cronRequestDto);
        return registry ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/registryGivenTask")
    @ResponseBody
    public ResponseDto registryGivenTask(@RequestBody SchedulerTaskCronRequestDto cronRequestDto) {
        boolean registry = registryTask.registry(cronRequestDto);
        return registry ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/deRegistryGivenTask")
    @ResponseBody
    public ResponseDto deRegistryGivenTask(@RequestBody SchedulerTaskCronRequestDto cronRequestDto) {
        boolean registry = registryTask.deRegistry(cronRequestDto);
        return registry ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
