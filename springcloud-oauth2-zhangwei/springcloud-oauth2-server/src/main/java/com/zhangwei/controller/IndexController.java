package com.zhangwei.controller;

import com.zhangwei.utils.OauthTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class IndexController {

    @Resource
    ResourceServerTokenServices tokenServices;

    @PostMapping("/index")
    public Authentication index(Authentication authentication) {
        return authentication;
    }

    @PostMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/userInfo")
    public Authentication userInfo(HttpServletRequest req) {
        return OauthTokenUtils.analysisAuthorization(req);
    }

    @GetMapping(value = "/user")
    public Principal user(HttpServletRequest req) throws IOException {
        return OauthTokenUtils.analysisPrincipal(req, tokenServices);
    }
}
