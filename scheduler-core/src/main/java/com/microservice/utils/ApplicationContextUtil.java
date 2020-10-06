package com.microservice.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 * springboot 上下文工具类
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT;

    public static <T> T getBean(Class<T> clazz) {
        try {
            return APPLICATION_CONTEXT.getBean(clazz);
        } catch (BeansException e) {
            return null;
        }
    }

    public static Collection getBeansOfType(Class clazz) {
        try {
            Map beansOfType = APPLICATION_CONTEXT.getBeansOfType(clazz);
            if (null == beansOfType) {
                return null;
            }

            Collection values = beansOfType.values();
            if (CollectionUtils.isEmpty(values)) {
                return null;
            }
            return values;
        } catch (BeansException e) {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }
}
