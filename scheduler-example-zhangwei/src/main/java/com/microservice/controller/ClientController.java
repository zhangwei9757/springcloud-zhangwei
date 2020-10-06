package com.microservice.controller;

import com.microservice.protocol.ExecutorClient;
import com.microservice.dto.ResponseDto;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext applicationContext;

    @PostMapping(value = "/index/{message}", produces = "application/x-protobuf")
    @ResponseBody
    public Object index(@PathVariable(value = "message") String message) {
        Channel channel = executorClient.getChannel();
//        MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol
//                .newBuilder()
//                .setLen(message.getBytes(Charset.defaultCharset()).length)
//                .setContent(message)
//                .build();
//        channel.writeAndFlush(messageProtocol);

        // 获取所有注解的类，

        return JsonUtils.toJson(null);
    }

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
}
