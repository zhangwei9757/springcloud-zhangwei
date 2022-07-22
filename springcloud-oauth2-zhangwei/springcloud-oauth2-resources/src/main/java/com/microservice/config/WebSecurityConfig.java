package com.microservice.config;

import com.microservice.security.AuthAccessDeniedHandler;
import com.microservice.security.AuthenticationEntryPointHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private FilterInvocationSecurityMetadataSource authFilterInvocationSecurityMetadataSource;
    @Autowired(required = false)
    private AccessDecisionManager authAccessDecisionManager;
    @Autowired(required = false)
    private AuthenticationEntryPointHandler authenticationEntryPoint;
    @Autowired(required = false)
    private AuthAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (Objects.nonNull(authFilterInvocationSecurityMetadataSource)) {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll()
                    .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                        @Override
                        public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                            o.setSecurityMetadataSource(authFilterInvocationSecurityMetadataSource);
                            o.setAccessDecisionManager(authAccessDecisionManager);
                            return o;
                        }
                    })
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            ;
        } else {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            ;
        }
    }
}
