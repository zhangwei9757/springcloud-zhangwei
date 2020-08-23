package com.zhangwei.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhangwei
 * @date 2020-08-21
 * <p>
 */
@Controller
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "websokcet ....";
    }

    @GetMapping("/index")
    public String websocket() {
        return "websocket";
    }

    @GetMapping("/online")
    public String online() {
        return "EasySwoole-WebSocket在线测试工具";
    }
}
