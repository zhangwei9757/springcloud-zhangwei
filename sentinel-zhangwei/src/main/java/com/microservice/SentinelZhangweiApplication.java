package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * step1: 接入 Sentinel
 * https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D
 * <p>
 * step2: 接入Sentinel 控制台
 * https://github.com/alibaba/Sentinel/wiki/%E6%8E%A7%E5%88%B6%E5%8F%B0
 * <p>
 * 控制台源码地址
 * https://github.com/alibaba/Sentinel/releases/tag/v1.8.0
 * 启动命令
 * java -Dserver.port=8080 \
 * -Dcsp.sentinel.dashboard.server=localhost:8080 \
 * -Dproject.name=sentinel-dashboard \
 * -jar target/sentinel-dashboard.jar
 * 参数	作用
 * -Dcsp.sentinel.dashboard.server=localhost:8080	向 Sentinel 接入端指定控制台的地址
 * -Dproject.name=sentinel-dashboard	向 Sentinel 指定应用名称，比如上面对应的应用名称就为 sentinel-dashboard
 * <p>
 * step3: 开启限流配置
 * https://github.com/alibaba/Sentinel/wiki/%E4%B8%BB%E6%B5%81%E6%A1%86%E6%9E%B6%E7%9A%84%E9%80%82%E9%85%8D
 */
@SpringBootApplication
public class SentinelZhangweiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelZhangweiApplication.class, args);
    }

    /**
     * Spring Cloud Sentinel 限流接入文档
     * https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel#%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8-sentinel
     */
}
