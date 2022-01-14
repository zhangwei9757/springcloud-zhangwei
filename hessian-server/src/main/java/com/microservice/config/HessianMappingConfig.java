package com.microservice.config;

import com.microservice.hessian.AccountHessian;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

/**
 * @author zw
 * @date 2022-01-14
 * <p>
 */
@Configuration
public class HessianMappingConfig {

    @Autowired
    private AccountHessian accountHessian;

    @Bean(name = "/hessian")
    public HessianServiceExporter accountService() {
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(accountHessian);
        exporter.setServiceInterface(AccountHessian.class);
        return exporter;
    }
}
