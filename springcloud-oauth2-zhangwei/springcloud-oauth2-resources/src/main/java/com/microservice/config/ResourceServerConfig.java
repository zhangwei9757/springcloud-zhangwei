package com.microservice.config;

import com.microservice.beans.JsonResult;
import com.microservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@Configuration
@EnableResourceServer
@Slf4j
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String SIGNING_KEY = "secret";
    private static final String RESOURCE_ID = "zhangwei";

    @Resource
    private TokenStore tokenStore;

    @Bean
    public TokenStore tokenStore(DataSource dataSource) {
        // return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
//        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources
                .resourceId(RESOURCE_ID)
                .tokenStore(tokenStore)
//                .tokenStore(new JwtTokenStore(accessTokenConverter()))
                .tokenServices(tokenServices())
                .accessDeniedHandler((request, response, e) -> {
                    log.error(">>>>>> 资源服务器认证失败, {}", e.getMessage(), e);

                    JsonResult<String> result = new JsonResult<>();
                    result.setFlag(false);
                    result.setCode(HttpStatus.FORBIDDEN.value());
                    result.setError(e.getMessage());

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter()
                            .write(Objects.requireNonNull(JsonUtils.toJson(result)));
                })
                .authenticationEntryPoint((request, response, e) -> {
                    log.error(">>>>>> 资源服务器匿名认证失败, {}", e.getMessage(), e);

                    JsonResult<String> result = new JsonResult<>();
                    result.setFlag(false);
                    result.setCode(HttpStatus.UNAUTHORIZED.value());
                    result.setError(e.getMessage());

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter()
                            .write(Objects.requireNonNull(JsonUtils.toJson(result)));
                })
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        services.setCheckTokenEndpointUrl("http://localhost:8766/oauth/check_token");
        services.setClientId("test_1");
        services.setClientSecret("123456");
        return services;
    }


//    @Bean
//    public ResourceServerTokenServices tokenServices() {
//        return new UserInfoTokenServices("http://localhost:8766/oauth/check_token", "test_1");
//    }


//    @Bean
//    public ResourceServerTokenServices tokenServices() {
//
//        // 配置RemoteTokenServices，用于向AuththorizationServer验证token
//        RemoteTokenServices tokenServices = new RemoteTokenServices();
//        tokenServices.setAccessTokenConverter(accessTokenConverter());
//
//        // 为restTemplate配置异常处理器，忽略400错误，
//        RestTemplate restTemplate = restTemplate();
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            // Ignore 400
//            public void handleError(ClientHttpResponse response) throws IOException {
//                if (response.getRawStatusCode() != 400) {
//                    super.handleError(response);
//                }
//            }
//        });
//        tokenServices.setRestTemplate(restTemplate);
//
//        tokenServices.setCheckTokenEndpointUrl("http://AUTHORIZATION-SERVER/oauth/check_token");
//
//        tokenServices.setClientId("client");
//        tokenServices.setClientSecret("secret");
//        return tokenServices;
//
//    }
}
