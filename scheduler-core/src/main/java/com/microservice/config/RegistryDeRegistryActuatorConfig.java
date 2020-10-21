package com.microservice.config;

import com.microservice.handler.actuator.DefaultRegistryDeRegistryActuator;
import com.microservice.handler.actuator.RegistryDeRegistryActuator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zw
 * @date 2020-10-21
 * <p>
 */
@Configuration
public class RegistryDeRegistryActuatorConfig {

    @Bean
    @ConditionalOnMissingBean(RegistryDeRegistryActuator.class)
    public RegistryDeRegistryActuator registryDeRegistryActuator() {
        return new DefaultRegistryDeRegistryActuator();
    }
}
