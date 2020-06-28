package com.zhangwei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String SIGNING_KEY = "secret";
    private static final String RESOURCE_ID = "zhangwei";

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources
                .resourceId(RESOURCE_ID)
                .tokenStore(tokenStore)
//                .tokenStore(new JwtTokenStore(accessTokenConverter()))
//                .tokenServices(tokenServices())
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
//                .antMatchers("/**").access("#oauth2.hasScope('all')")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public TokenStore tokenStore() {
        // return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
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
        services.setClientId("admin");
        services.setClientSecret("123456");
        return services;
    }


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
