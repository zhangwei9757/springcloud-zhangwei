package com.zhangwei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final static String SECRET = "secret";

    /**
     * 令牌策略
     */
    @Autowired
    private TokenStore tokenStore;

    /**
     * 客户端详情服务
     */
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 认证管理器 , 密码模式需要
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 授权码模式, 需要
     */
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    /**
     * 密码处理器
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApprovalStore approvalStore;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
//        clients
//                .jdbc(dataSource)
//                .passwordEncoder(passwordEncoder)
//                .clients(clientDetailsService());

//        clients.inMemory()
//                .withClient("user")
//                .resourceIds("zhangwei")
//                .secret(new BCryptPasswordEncoder().encode("123456"))
//                .authorizedGrantTypes("authorization_code", "refresh_token")
//                .scopes("all")
//                .autoApprove(false)
//                .redirectUris("http://localhost:8086/login")
//                .and()
//
//                .withClient("admin")
//                .resourceIds("zhangwei")
//                .secret(new BCryptPasswordEncoder().encode("123456"))
//                .authorizedGrantTypes("authorization_code", "password", "refresh_token", "client_credentials", "implicit")
//                .scopes("all")
//                .autoApprove(false)
//                .redirectUris("http://www.baidu.com");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .approvalStore(approvalStore)
                // .userDetailsService(userDetailsService)
                // 认证管理器
                .authenticationManager(authenticationManager)
                // 授权码管理器
                .authorizationCodeServices(authorizationCodeServices)
                // 令牌管理器
                .tokenServices(tokenServices())
                // 允许post 请求令牌
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);

//        DefaultTokenServices tokenServices = (DefaultTokenServices) endpoints.getDefaultAuthorizationServerTokenServices();
//        tokenServices.setTokenStore(tokenStore());
//        tokenServices.setSupportRefreshToken(true);
//        //获取ClientDetailsService信息
//        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//        tokenServices.setTokenEnhancer(accessTokenConverter());
//        // 一天有效期
//        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1));
//        endpoints.tokenServices(tokenServices);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .passwordEncoder(passwordEncoder)
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 令牌管理服务
     *
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices service = new DefaultTokenServices();
        // 客户端详情 服务
        service.setClientDetailsService(clientDetailsService);
        // 是否刷新令牌
        service.setSupportRefreshToken(true);
        // 令牌策略
        service.setTokenStore(tokenStore);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(accessTokenConverter()));
        service.setTokenEnhancer(tokenEnhancerChain);
        // 令牌有效期
        service.setAccessTokenValiditySeconds(7200);
        // 刷新令牌时间
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }

    @Bean
    public TokenStore tokenStore(DataSource dataSource) {
        return new JdbcTokenStore(dataSource);
//        return new InMemoryTokenStore();
//        return redisTokenStore;
    }

    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SECRET);
        return converter;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        //设置授权码模式的授权码如何存取
        return new JdbcAuthorizationCodeServices(dataSource);
//        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public ApprovalStore approvalStore(DataSource dataSource) {
        return new JdbcApprovalStore(dataSource);
    }

    private static RestTemplate getInstance(String charset, RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate;
    }
}
