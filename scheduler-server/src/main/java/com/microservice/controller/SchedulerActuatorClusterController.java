package com.microservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.handler.actuator.RegistryDeRegistryActuator;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.utils.Constants;
import com.microservice.utils.ErrCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-15
 */
@RestController
@RequestMapping("/scheduler-actuator")
@Api(value = "执行器集群管理", tags = "执行器集群管理")
public class SchedulerActuatorClusterController {

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Autowired
    private RegistryDeRegistryActuator registryDeRegistryActuator;

    @GetMapping(value = "/allActuators")
    @ApiOperation(value = "执行器集群列表[全量]", tags = "执行器集群管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "registryDetailRequestDto", required = false, value = "查询条件", dataType = "SchedulerRegistryDetailRequestDto", paramType = "body"),
    })
    public ResponseDto allActuators() {
        List<SchedulerRegistryDetailRequestDto> actuators = registryDetailService.allActuators();
        return ResponseDto.success(actuators);
    }

    @PostMapping(value = "/allActuatorsPage")
    @ApiOperation(value = "执行器集群列表[分页]", tags = "执行器集群管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "registryDetailRequestDto", required = false, value = "查询条件", dataType = "SchedulerRegistryDetailRequestDto", paramType = "body"),
    })
    public ResponseDto allActuatorsPage(@RequestBody SchedulerRegistryDetailRequestDto registryDetailRequestDto) {

        if (Objects.isNull(registryDetailRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        IPage<SchedulerRegistryDetailRequestDto> page = registryDetailService.allActuatorsPage(registryDetailRequestDto);
        return ResponseDto.success(page);
    }

    @PostMapping(value = "/actuatorOnLineOffLine")
    @ApiOperation(value = "执行器上线/离线", tags = "执行器集群管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "registryDetailRequestDto", required = true, value = "执行器信息", dataType = "SchedulerRegistryDetailRequestDto", paramType = "body"),
    })
    public ResponseDto actuatorOnLineOffLine(@RequestBody SchedulerRegistryDetailRequestDto registryDetailRequestDto) {

        if (Objects.isNull(registryDetailRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        String appName = registryDetailRequestDto.getRegisterDetailAppName();
        String ip = registryDetailRequestDto.getRegisterDetailIp();
        String port = registryDetailRequestDto.getRegisterDetailPort();

        if (StringUtils.isAnyBlank(appName, ip, port)) {
            return ResponseDto.error(ErrCode.PARAMS_WRONGFUL);
        }

        SchedulerRegistryDetailRequestDto resultDto = registryDetailService.findRegistryDetailByAppNameAndIpAndPort(registryDetailRequestDto);
        if (Objects.isNull(resultDto)) {
            return ResponseDto.error(ErrCode.NOT_FOUND_RESULT);
        }

        // 目标状态
        String status = registryDetailRequestDto.getRegisterDetailStatus();
        // 数据状态
        String registerStatus = resultDto.getRegisterDetailStatus();

        // 无论状态是否一致强制修改，为了保证 ACP 强一致性
        boolean onLine = Objects.deepEquals(status, Constants.ON_LINE);

        if (onLine) {
            boolean success = this.registryDeRegistryActuator.registryActuator(registryDetailRequestDto);
            return success ? ResponseDto.success() : ResponseDto.error(ErrCode.OFF_LINE);
        } else {
            boolean success = this.registryDeRegistryActuator.deRegistryActuator(registryDetailRequestDto);
            return success ? ResponseDto.success() : ResponseDto.error(ErrCode.OFF_LINE);
        }
    }
}
