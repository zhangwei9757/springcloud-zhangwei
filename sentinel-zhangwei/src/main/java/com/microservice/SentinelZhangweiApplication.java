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
 */
@SpringBootApplication
public class SentinelZhangweiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelZhangweiApplication.class, args);
    }

}
