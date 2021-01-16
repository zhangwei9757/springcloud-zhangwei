package com.microservice.server;

import com.microservice.annotation.ExecutorJob;
import com.microservice.dto.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@Component
@Slf4j
public class TestJob {

    @ExecutorJob(value = "test", init = "init", destroy = "destroy")
    public ReturnT test(String param) {
        log.info(">>> TestJob.test 执行成功， 参数: {}", param);
        return ReturnT.SUCCESS;
    }

    public void init() {
        System.out.println("test init method");
    }

    public void destroy() {
        System.out.println("test destroy method");
    }
}
