package com.microservice.config;

import com.microservice.utils.RestTemplateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangwei
 * @date 2020-06-05
 * <p>
 */
@Slf4j
@Data
@Configuration
public class ApplicationContextConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        log.info(" --->>> 应用程序上下文 ： [{}]", "开始初始化");
        RestTemplateUtils.applicationContext = applicationContext;
        assert applicationContext != null;
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        RestTemplateUtils.setRestTemplate(restTemplate);

        log.info(" --->>> 应用程序上下文 getId ： [{}]", applicationContext.getId());
        log.info(" --->>> 应用程序上下文 getApplicationName ： [{}]", applicationContext.getApplicationName());
        log.info(" --->>> 应用程序上下文 getAutowireCapableBeanFactory ： [{}]", applicationContext.getAutowireCapableBeanFactory());
        log.info(" --->>> 应用程序上下文 getDisplayName ： [{}]", applicationContext.getDisplayName());
        log.info(" --->>> 应用程序上下文 getParent ： [{}]", applicationContext.getParent());
        log.info(" --->>> 应用程序上下文 getStartupDate ： [{}]", applicationContext.getStartupDate());
        log.info(" --->>> 应用程序上下文 getEnvironment ： [{}]", applicationContext.getEnvironment());
        log.info(" --->>> 应用程序上下文 ： [{}]", "初始化完成");
    }
}