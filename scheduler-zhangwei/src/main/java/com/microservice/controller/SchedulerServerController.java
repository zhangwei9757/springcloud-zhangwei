package com.microservice.controller;

import com.microservice.bean.ActuatorHealthCheckResult;
import com.microservice.dto.ResponseDto;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RestTemplateUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerServerController {

//    @Autowired
//    private ExecutorClient executorClient;
//
//    @PostMapping(value = "/index/{message}", produces = "application/x-protobuf")
//    @ResponseBody
//    public MessageProtocolPoJo.MessageProtocol index(@PathVariable(value = "message") String message) {
//        Channel channel = executorClient.getChannel();
//        MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol
//                .newBuilder()
//                .setLen(message.getBytes(Charset.defaultCharset()).length)
//                .setContent(message)
//                .build();
//        channel.writeAndFlush(messageProtocol);
//        return messageProtocol;
//    }

    @GetMapping(value = "/index")
    @ResponseBody
    public String register() {
        return "index";
    }

    @GetMapping(value = "/healthCheck")
    @ResponseBody
    public ResponseDto healthCheck() {
        ResponseEntity<String> responseEntity = RestTemplateUtils.get("http://192.168.40.1:8001/actuator/health", String.class);
        String body = responseEntity.getBody();
        ActuatorHealthCheckResult checkResult = JsonUtils.fromJson(body, ActuatorHealthCheckResult.class);
        return ResponseDto.success(checkResult);
    }
}
