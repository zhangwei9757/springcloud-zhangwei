package com.microservice.scheduler;

import com.microservice.annotation.ExecutorJob;
import com.microservice.dto.ReturnT;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@Component
public class TestJob {

    @ExecutorJob(value = "test", init = "init", destroy = "destroy")
    public ReturnT test(String param) {
        return ReturnT.SUCCESS;
    }

    public void init() {
        System.out.println("test init method");
    }

    public void destroy() {
        System.out.println("test destroy method");
    }
}
