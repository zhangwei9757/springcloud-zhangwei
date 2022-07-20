package com.microservice.config;

import com.caucho.hessian.client.HessianProxyFactory;
import com.microservice.hessian.AccountHessian;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import java.net.MalformedURLException;

/**
 * @author zw
 * @date 2022-01-14
 * <p>
 */
@Configuration
public class HessianConfig {

    @Bean
    @Profile("prod")
    public RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }

    @Bean
    public HessianProxyFactoryBean hessianProxyFactoryBean() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl("http://localhost:9999/hessian");
        factory.setServiceInterface(AccountHessian.class);

        return factory;
    }

    public static <T> T getHessianClientBean(Class<T> clazz, String url) {
        // 客户端连接工厂,这里只是做了最简单的实例化，还可以设置超时时间，密码等安全参数
        HessianProxyFactory factory = new HessianProxyFactory();
        Object o = null;
        try {
            o = factory.create(clazz, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return (T) o;
    }
}
