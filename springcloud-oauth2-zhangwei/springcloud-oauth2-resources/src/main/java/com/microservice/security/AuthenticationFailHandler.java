package com.microservice.security;

import com.microservice.beans.JsonResult;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2022-06-20
 */
@Service
@Slf4j
public class AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        log.error("用户认证失败, {}", exception.getMessage());

        // 返回json数据
        JsonResult result = new JsonResult();
        result.setCode(HttpStatus.FORBIDDEN.value());
        result.setError("用户认证失败");
        if (exception instanceof AccountExpiredException) {
            // 账号过期
        } else if (exception instanceof BadCredentialsException) {
            // 密码错误
        } else if (exception instanceof CredentialsExpiredException) {
            // 密码过期
        } else if (exception instanceof DisabledException) {
            // 账号不可用
        } else if (exception instanceof LockedException) {
            // 账号锁定
        } else if (exception instanceof InternalAuthenticationServiceException) {
            // 用户不存在
        } else {
            // 其他错误
        }

        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(Objects.requireNonNull(JsonUtils.toJson(result)));
        } catch (IOException e) {
            log.error("认证失败解析返回JSON异常, {}", e.getMessage(), e);
        }
    }
}