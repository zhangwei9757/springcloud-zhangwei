package com.microservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.service.ISchedulerTaskLogService;
import com.microservice.utils.ErrCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhangwei
 * @since 2020-10-16
 */
@RestController
@RequestMapping("/scheduler-task-log")
@Api(value = "执行器任务管理", tags = "执行器任务管理")
public class SchedulerActuatorTaskLogController {

    @Autowired
    private ISchedulerTaskLogService taskLogService;

    @PostMapping(value = "/taskLogs")
    @ApiOperation(value = "任务日志列表[带分页]", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskLogRequestDto", required = false, value = "查询条件", dataType = "SchedulerTaskLogRequestDto", paramType = "body"),
    })
    public ResponseDto taskLogs(@RequestBody SchedulerTaskLogRequestDto taskLogRequestDto) {
        if (Objects.isNull(taskLogRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        IPage<SchedulerTaskLogRequestDto> tasks = taskLogService.findTaskLogsByParams(taskLogRequestDto);
        return ResponseDto.success(tasks);
    }
}
