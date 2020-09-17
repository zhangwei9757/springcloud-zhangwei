package com.zhangwei.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2020-09-14
 * <p>
 */
@RestController
public class IndexController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
