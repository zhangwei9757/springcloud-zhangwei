package com.microservice.handler.task;

import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.RandomUtils;
import com.microservice.utils.RestTemplateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

/**
 * @author zw
 * @date 2020-10-19
 * <p>
 */
public abstract class AbstractRegistryDeRegistryTask implements RegistryDeRegistryTask {

    private final String baseUrl = "http://%s/scheduler-task/%s";

    @Override
    public boolean registry(SchedulerTaskCronRequestDto cronTask) {
        RedisDefaultClientHandler defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        Set<String> allWebApi = defaultClientHandler.findAllWebApi();
        String ipPort = RandomUtils.getInList(allWebApi);
        String url = String.format(baseUrl, ipPort, "cronRegistryTask");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.post(url, cronTask, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean registry(SchedulerTaskGivenRequestDto givenTask) {
        RedisDefaultClientHandler defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        Set<String> allWebApi = defaultClientHandler.findAllWebApi();
        String ipPort = RandomUtils.getInList(allWebApi);
        String url = String.format(baseUrl, ipPort, "givenRegistryTask");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.post(url, givenTask, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deRegistry(SchedulerTaskCronRequestDto cronTask) {
        RedisDefaultClientHandler defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        Set<String> allWebApi = defaultClientHandler.findAllWebApi();
        String ipPort = RandomUtils.getInList(allWebApi);
        String url = String.format(baseUrl, ipPort, "cronDelTask");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.post(url, cronTask, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deRegistry(SchedulerTaskGivenRequestDto givenTask) {
        RedisDefaultClientHandler defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        Set<String> allWebApi = defaultClientHandler.findAllWebApi();
        String ipPort = RandomUtils.getInList(allWebApi);
        String url = String.format(baseUrl, ipPort, "givenDelTask");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.post(url, givenTask, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }
}
