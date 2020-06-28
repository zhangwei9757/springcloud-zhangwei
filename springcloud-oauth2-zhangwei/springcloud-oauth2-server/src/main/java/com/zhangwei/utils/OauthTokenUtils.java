package com.zhangwei.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author zhangwei
 * @date 2020-06-21
 * <p>
 * OAuth2 token解析工具
 */
public class OauthTokenUtils {

    /**
     * 解析 oauth2 Authentication
     *
     * @param request 请求
     * @return Authentication
     */
    public static Authentication analysisAuthorization(HttpServletRequest request) {
        BearerTokenExtractor tokenExtractor = new BearerTokenExtractor();
        return tokenExtractor.extract(request);
    }

    /**
     * 解析 oauth2 Principal
     *
     * @param request       请求
     * @param tokenServices 资源服务
     * @return Principal
     */
    public static Principal analysisPrincipal(HttpServletRequest request, ResourceServerTokenServices tokenServices) {
        Authentication authentication = analysisAuthorization(request);
        String token = (String) authentication.getPrincipal();
        return tokenServices.loadAuthentication(token);
    }
}
