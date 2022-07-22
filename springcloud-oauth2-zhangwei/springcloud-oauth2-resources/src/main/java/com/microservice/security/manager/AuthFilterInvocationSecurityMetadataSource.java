package com.microservice.security.manager;

import com.microservice.annotation.annotations.EnableGlobalWebSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author zhangwei
 * @date 2022-06-20
 */
@Configuration
@ConditionalOnBean(annotation = EnableGlobalWebSecurity.class)
@Slf4j
public class AuthFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Bean
    public AntPathMatcher getAntPathMatcher() {
        return new AntPathMatcher();
    }

    @Autowired
    private AntPathMatcher antPathMatcher;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) o;
        String url = fi.getRequestUrl();
        String uri = URI.create(url).getPath();

        log.info(">>>>>> 动态待校验权限 uri: {}", uri);


        Collection<ConfigAttribute> rs = new ArrayList<>(0);

//        for (Object resourceVo : vos) {
//            String realUrl = resourceVo.getUrl();
//            Long resourceId = resourceVo.getResourceId();
//            if (StringUtils.isNotBlank(realUrl)) {
//                if (antPathMatcher.match(realUrl, uri)) {
//                    rs.add((ConfigAttribute) resourceId::toString);
//                }
//            }
//        }

        return rs;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
