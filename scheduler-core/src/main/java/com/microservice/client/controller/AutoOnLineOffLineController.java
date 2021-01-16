package com.microservice.client.controller;

import com.microservice.client.ExecutorClient;
import com.microservice.dto.ResponseDto;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.Constants;
import com.microservice.utils.ErrCode;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Executor;

/**
 * @author zw
 * @date 2020-10-20
 * <p>
 * 执行器自动 [上线，离线]
 */
@RestController
@RequestMapping(value = "/actuator")
@Slf4j
public class AutoOnLineOffLineController {

    @Autowired
    private Executor executor;

    @PostMapping(value = "/onLine")
    public ResponseDto onLine() {
        ApplicationContext applicationContext = ApplicationContextUtil.APPLICATION_CONTEXT;
        ExecutorClient client = applicationContext.getBean(ExecutorClient.class);
        Channel channel = client.getChannel();
        Channel flush = channel.flush();
        boolean active = flush.isActive();
        if (active) {
            return ResponseDto.error(ErrCode.ON_LINE);
        }
        client.retries = 0;
        try {
            executor.execute(() -> {
                try {
                    client.connect();
                } catch (Exception e) {
                    log.info(">>> RPC 通知当前执行器, 自动上线失败, 原因: {}", e.getLocalizedMessage(), e);
                }
            });
            log.info(">>> RPC 通知当前执行器, 自动上线......");
            return ResponseDto.success();
        } catch (Exception e) {
            return ResponseDto.error(e.getLocalizedMessage());
        }
    }

    @DeleteMapping(value = "/offLine")
    public ResponseDto offLine() {
        ApplicationContext applicationContext = ApplicationContextUtil.APPLICATION_CONTEXT;
        ExecutorClient client = applicationContext.getBean(ExecutorClient.class);
        client.retries = Integer.MAX_VALUE;
        Channel channel = client.getChannel();
        Channel flush = channel.flush();
        boolean active = flush.isActive();
        if (active) {
            channel.close();
            log.info(">>> RPC 通知当前执行器, 自动下线......");
            flush = channel.flush();
        }
        return ResponseDto.success(flush);
    }

    @GetMapping(value = Constants.ACTUATOR_HEALTH_CHECK_URL)
    public ResponseDto actuatorHealthCheckUrl() {
        ApplicationContext applicationContext = ApplicationContextUtil.APPLICATION_CONTEXT;
        ExecutorClient client = applicationContext.getBean(ExecutorClient.class);
        Channel channel = client.getChannel();
        Channel flush = channel.flush();
        boolean active = flush.isActive();
        if (active) {
            return ResponseDto.success();
        } else {
            return ResponseDto.error();
        }
    }
}
