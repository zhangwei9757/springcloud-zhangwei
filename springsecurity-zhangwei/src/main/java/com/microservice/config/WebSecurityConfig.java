package com.microservice.config;

import com.microservice.security.JwtAuthenticationFilter;
import com.microservice.security.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //下面这两行配置表示在内存中配置了两个用户
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .roles("admin")
//                .password("$2a$10$OR3VSksVAmCzc.7WeaRPR.t0wyCsIj24k0Bne8iKWV1o.V9wsP8Xe")
//                .and()
//                .withUser("zhangwei")
//                .roles("user")
//                .password("$2a$10$p1H8iWa8I4.CA.7Z8bwLjes91ZpY.rYREGHQEInNtAp4NzL6PLKxi");
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http//.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs/**", "/druid/**", "/api/login", "/api/logout", "/oauth/**", "/user/register", "/xxljob/**", "/actuator/**")
                .permitAll()
                .antMatchers("/**/*.css", "/**/*.html", "/**/*.js", "/**/*.jpg", "/**/webjars/**", "/login", "/**/bootstrap/**")
                .permitAll()
                .antMatchers("/user-sub/findUserList", "/user-sub/findUserSubs")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .authorizeRequests()
//                    .anyRequest()
//                    .access("@rbacauthorityservice.hasPermission(request,authentication)")
                .and()
                .formLogin().loginProcessingUrl("/api/login")
//                .successHandler(authenticationSuccessHandler)
//                .failureHandler(authenticationFailHandler)
                .and()
                .logout().logoutUrl("/api/logout")
//                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPoint)
//                .accessDeniedHandler(accessDeniedHandler)
        ;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}