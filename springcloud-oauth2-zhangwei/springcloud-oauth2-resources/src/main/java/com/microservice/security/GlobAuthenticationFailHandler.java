package com.microservice.security;

import com.microservice.beans.JsonResult;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class GlobAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        log.error("用户认证失败, {}", exception.getMessage());


        try {
            JsonResult result = new JsonResult();
            result.setCode(HttpStatus.UNAUTHORIZED.value());
            result.setError("用户认证失败Global");

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(Objects.requireNonNull(JsonUtils.toJson(result)));
        } catch (IOException e) {
            log.error("认证失败解析返回JSON异常, {}", e.getMessage(), e);
        }
    }
}