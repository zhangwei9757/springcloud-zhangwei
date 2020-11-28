package com.microservice.controller;

import com.microservice.bean.MessageProtocolPoJo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author zw
 * @date 2020-11-26
 * <p>
 */
@RestController
@Api(value = "测试", tags = "测试")
public class IndexController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/hello")
    @ApiOperation(value = "hello", tags = "测试")
    public String hello() {
        return "正常响应";
    }

    @PostMapping(value = "/index")
//    @ApiOperation(value = "index", tags = "测试")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "a", required = false, value = "查询条件", dataType = "MessageProtocolPoJo.MessageProtocol", paramType = "body"),
//    })
    public String index(@RequestBody MessageProtocolPoJo.MessageProtocol a) {
        System.out.println(a);
        MessageProtocolPoJo.MessageProtocol build = MessageProtocolPoJo.MessageProtocol.newBuilder()
                .setContent("测试响应内容")
                .build();
        return "index";
    }

    @GetMapping(value = "/test", produces = "application/x-protobuf")
//    @ApiOperation(value = "test", tags = "测试")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "registryDetailRequestDto", required = false, value = "查询条件", dataType = "SchedulerRegistryDetailRequestDto", paramType = "body"),
//    })
    public String test() {
        MessageProtocolPoJo.MessageProtocol build = MessageProtocolPoJo.MessageProtocol.newBuilder()
                .setContent("测试请求内容")
                .build();
        String result = restTemplate.postForObject("http://127.0.0.1:9000/index", build, String.class);
        System.out.println(result);
        return "test";
    }
}
