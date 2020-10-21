package com.microservice.handler.actuator;

import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.utils.RestTemplateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author zw
 * @date 2020-10-21
 * <p>
 */
public class DefaultRegistryDeRegistryActuator implements RegistryDeRegistryActuator {

    private final String baseUrl = "http://%s:%s/actuator/%s";

    @Override
    public boolean registryActuator(SchedulerRegistryDetailRequestDto registryDetailDto) {
        String ip = registryDetailDto.getRegisterDetailIp();
        String port = registryDetailDto.getRegisterDetailPort();
        String url = String.format(baseUrl, ip, port, "onLine");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.post(url, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deRegistryActuator(SchedulerRegistryDetailRequestDto registryDetailDto) {
        String ip = registryDetailDto.getRegisterDetailIp();
        String port = registryDetailDto.getRegisterDetailPort();
        String url = String.format(baseUrl, ip, port, "offLine");
        try {
            ResponseEntity<ResponseDto> post = RestTemplateUtils.delete(url, ResponseDto.class);
            HttpStatus statusCode = post.getStatusCode();
            return HttpStatus.OK == statusCode;
        } catch (Exception e) {
            return false;
        }
    }
}
