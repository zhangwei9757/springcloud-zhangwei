package com.zhangwei.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@RestController
public class ResourceController {

    @Resource
    ResourceServerTokenServices tokenServices;

    @GetMapping("/info")
    public Object get() {
        return "resource server...";
    }

    @GetMapping("/user")
    public Authentication user(Authentication authentication) {
        return authentication;
    }

    @GetMapping(value = "/userInfo")
    public Principal userInfo(ServletRequest req) throws IOException {
        final HttpServletRequest request = (HttpServletRequest) req;
        BearerTokenExtractor tokenExtractor = new BearerTokenExtractor();
        Authentication authentication = tokenExtractor.extract(request);
        String token = (String) authentication.getPrincipal();
        return tokenServices.loadAuthentication(token);
    }
}


