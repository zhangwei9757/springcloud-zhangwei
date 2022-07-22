package com.microservice.security;

import com.microservice.beans.JsonResult;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author zhangwei
 * @date 2022-06-20
 */
@Service
@Slf4j
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {
        log.error("用户匿名访问操作, {}", e.getMessage(), e);

        JsonResult result = new JsonResult();
        result.setCode(HttpStatus.UNAUTHORIZED.value());
        result.setError("用户匿名访问操作");

        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JsonUtils.toJson(result));
    }
}
