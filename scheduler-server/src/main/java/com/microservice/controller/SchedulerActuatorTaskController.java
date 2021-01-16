package com.microservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microservice.dto.ResponseDto;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.service.ISchedulerTaskCronService;
import com.microservice.service.ISchedulerTaskGivenService;
import com.microservice.utils.ErrCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * @since 2020-10-15
 */
@RestController
@RequestMapping("/scheduler-task")
@Api(value = "执行器任务管理", tags = "执行器任务管理")
public class SchedulerActuatorTaskController {

    @Autowired
    private ISchedulerTaskCronService taskCronService;

    @Autowired
    private ISchedulerTaskGivenService taskGivenService;

    /**
     * ---------------------------------------------------------- Cron 任务相关 -----------------------------------
     *
     * @param taskCronRequestDto
     * @return
     */
    @PostMapping(value = "/cronTasks")
    @ApiOperation(value = "cron任务列表[带分页]", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskCronRequestDto", required = false, value = "查询条件", dataType = "SchedulerTaskCronRequestDto", paramType = "body"),
    })
    public ResponseDto cronTasks(@RequestBody SchedulerTaskCronRequestDto taskCronRequestDto) {

        if (Objects.isNull(taskCronRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        IPage<SchedulerTaskCronRequestDto> tasks = taskCronService.tasks(taskCronRequestDto);
        return ResponseDto.success(tasks);
    }

    @PostMapping(value = "/cronAllTask")
    @ApiOperation(value = "cron任务列表[全量]", tags = "执行器任务管理")
    public ResponseDto cronAllTask() {
        return ResponseDto.success(taskCronService.tasks());
    }


    @PostMapping(value = "/cronRegistryTask")
    @ApiOperation(value = "cron任务注册", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskCronRequestDto", required = false, value = "任务", dataType = "SchedulerTaskCronRequestDto", paramType = "body"),
    })
    public ResponseDto cronRegistryTask(@RequestBody SchedulerTaskCronRequestDto taskCronRequestDto) {
        boolean success = taskCronService.registryCronTask(taskCronRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/cronTaskStautsUpdate")
    @ApiOperation(value = "cron任务状态修改", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskCronRequestDto", required = false, value = "任务", dataType = "SchedulerTaskCronRequestDto", paramType = "body"),
    })
    public ResponseDto cronTaskStautsUpdate(@RequestBody SchedulerTaskCronRequestDto taskCronRequestDto) {
        boolean success = taskCronService.cronTaskStautsUpdate(taskCronRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/cronDelTask")
    @ApiOperation(value = "cron任务删除", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cronRequestDto", required = false, value = "任务", dataType = "SchedulerTaskCronRequestDto", paramType = "body"),
    })
    public ResponseDto cronDelTask(@RequestBody SchedulerTaskCronRequestDto cronRequestDto) {
        if (Objects.isNull(cronRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        boolean anyBlank = StringUtils.isAnyBlank(cronRequestDto.getTaskCronAppName(), cronRequestDto.getTaskCronJobHandler());
        if (anyBlank) {
            return ResponseDto.error(ErrCode.PARAMS_WRONGFUL);
        }

        boolean success = taskCronService.delCronTask(cronRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    /**
     * ---------------------------------------------------------- Given 任务相关 -----------------------------------
     *
     * @param givenRequestDto
     * @return
     */
    @PostMapping(value = "/givenTasks")
    @ApiOperation(value = "指定时间任务列表[带分页]", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "givenRequestDto", required = false, value = "查询条件", dataType = "SchedulerTaskGivenRequestDto", paramType = "body"),
    })
    public ResponseDto givenTasks(@RequestBody SchedulerTaskGivenRequestDto givenRequestDto) {

        if (Objects.isNull(givenRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        IPage<SchedulerTaskGivenRequestDto> tasks = taskGivenService.tasks(givenRequestDto);
        return ResponseDto.success(tasks);
    }

    @PostMapping(value = "/givenAllTask")
    @ApiOperation(value = "指定时间任务列表[全量]", tags = "执行器任务管理")
    public ResponseDto givenAllTask() {
        return ResponseDto.success(taskGivenService.tasks());
    }

    @PostMapping(value = "/givenRegistryTask")
    @ApiOperation(value = "given任务注册", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "givenRequestDto", required = false, value = "任务", dataType = "SchedulerTaskGivenRequestDto", paramType = "body"),
    })
    public ResponseDto givenRegistryTask(@RequestBody SchedulerTaskGivenRequestDto givenRequestDto) {
        boolean success = taskGivenService.registryGivenTask(givenRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/givenTaskStatusUpdate")
    @ApiOperation(value = "given任务状态修改", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "givenRequestDto", required = false, value = "任务", dataType = "SchedulerTaskGivenRequestDto", paramType = "body"),
    })
    public ResponseDto givenTaskStatusUpdate(@RequestBody SchedulerTaskGivenRequestDto givenRequestDto) {
        boolean success = taskGivenService.givenTaskStatusUpdate(givenRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @PostMapping(value = "/givenDelTask")
    @ApiOperation(value = "given任务删除", tags = "执行器任务管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "givenRequestDto", required = false, value = "任务", dataType = "SchedulerTaskGivenRequestDto", paramType = "body"),
    })
    public ResponseDto givenDelTask(@RequestBody SchedulerTaskGivenRequestDto givenRequestDto) {
        if (Objects.isNull(givenRequestDto)) {
            return ResponseDto.error(ErrCode.PARAMS_EMPTY);
        }

        boolean anyBlank = StringUtils.isAnyBlank(givenRequestDto.getTaskGivenAppName(), givenRequestDto.getTaskGivenJobHandler());
        if (anyBlank) {
            return ResponseDto.error(ErrCode.PARAMS_WRONGFUL);
        }

        boolean success = taskGivenService.registryGivenTask(givenRequestDto);
        return success ? ResponseDto.success() : ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
