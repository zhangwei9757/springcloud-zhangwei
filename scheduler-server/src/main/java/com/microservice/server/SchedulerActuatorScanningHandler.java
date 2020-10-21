package com.microservice.server;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.utils.Constants;
import com.microservice.utils.RestTemplateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zw
 * @date 2020-10-20
 * <p>
 * 执行器集群状态扫描
 */
@Service
public class SchedulerActuatorScanningHandler {

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Autowired
    private SchedulerConfigurationProperties properties;

    /**
     * 自动扫描执行器的状态, 状态未改变不修改
     *
     * @return
     */
    public boolean autoActuatorScanning() {
        List<SchedulerRegistryDetailRequestDto> registryDetailList = registryDetailService.allActuators();

        if (CollectionUtils.isEmpty(registryDetailList)) {
            return true;
        }
        Map<String, Boolean> healthCheckMap = new HashMap<>(registryDetailList.size());
        registryDetailList.parallelStream().forEach(f -> {
            String ipPort = f.getRegisterDetailIp() + ":" + f.getRegisterDetailPort();
            boolean healthCheckStatus;
            try {
                String url = "http://" + ipPort + Constants.ACTUATOR_HEALTH_CHECK_FULL_URL;
                ResponseEntity<ResponseDto> responseEntity = RestTemplateUtils.get(url, ResponseDto.class);
                ResponseDto body = responseEntity.getBody();
                int code = body.getCode();
                healthCheckStatus = Objects.deepEquals(code, HttpStatus.OK.value());
            } catch (Exception e) {
                healthCheckStatus = false;
            }

            healthCheckMap.put(ipPort, healthCheckStatus);
        });

        healthCheckMap.entrySet().parallelStream().forEach(entry -> {
            // 如果下线了，标记下线，如果上线了，标记已上线
            String ipPort = entry.getKey();
            Boolean status = entry.getValue();
            SchedulerRegistryDetailRequestDto registryDetailDto = registryDetailList.parallelStream()
                    .filter(f -> Objects.deepEquals(ipPort, f.getRegisterDetailIp() + ":" + f.getRegisterDetailPort()))
                    .findFirst()
                    .orElseGet(() -> null);

            if (Objects.nonNull(registryDetailDto)) {
                // 开始解析状态
                SchedulerRegistryDetailRequestDto updateDto = new SchedulerRegistryDetailRequestDto();
                BeanUtils.copyProperties(registryDetailDto, updateDto);

                // 无论是否状态一致都要检查真实状态与，保存的状态是否匹配
                if (status) {
                    // 表示真实状态为在线
                    LocalDateTime onlineTime = registryDetailDto.getRegisterDetailOnlineTime();
                    LocalDateTime offlineTime = registryDetailDto.getRegisterDetailOfflineTime();

                    // 表示丢失在线时间了，要重新赋值
                    if (Objects.isNull(onlineTime)) {
                        updateDto.setRegisterDetailId(registryDetailDto.getRegisterDetailId());
                        updateDto.setRegisterDetailStatus(Constants.ON_LINE);
                        updateDto.setRegisterDetailOnlineTime(LocalDateTime.now());
                        updateDto.setRegisterDetailOfflineTime(null);
                        registryDetailService.saveOrUpdate(updateDto);
                    }

                    // 表示离线时间是脏数据，要重新赋值
                    if (Objects.nonNull(offlineTime)) {
                        updateDto.setRegisterDetailId(registryDetailDto.getRegisterDetailId());
                        updateDto.setRegisterDetailStatus(Constants.ON_LINE);
                        updateDto.setRegisterDetailOnlineTime(onlineTime);
                        updateDto.setRegisterDetailOfflineTime(null);
                        registryDetailService.saveOrUpdate(updateDto);
                    }
                } else {
                    // 表示真实状态为离线
                    LocalDateTime offlineTime = registryDetailDto.getRegisterDetailOfflineTime();
                    LocalDateTime onlineTime = registryDetailDto.getRegisterDetailOnlineTime();

                    // 表示丢失离线时间了，要重新赋值
                    if (Objects.isNull(offlineTime)) {
                        updateDto.setRegisterDetailId(registryDetailDto.getRegisterDetailId());
                        updateDto.setRegisterDetailStatus(Constants.OFF_LINE);
                        updateDto.setRegisterDetailOnlineTime(onlineTime);
                        updateDto.setRegisterDetailOfflineTime(LocalDateTime.now());
                        registryDetailService.saveOrUpdate(updateDto);
                    }
                }
            }
        });
        return false;
    }
}
