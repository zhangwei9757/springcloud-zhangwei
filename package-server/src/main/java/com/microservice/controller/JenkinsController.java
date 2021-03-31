package com.microservice.controller;

import com.microservice.bean.JsonResult;
import com.microservice.jenkins.JenkinsConnect;
import com.offbytwo.jenkins.JenkinsServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zw
 * @date 2021-03-14
 * <p>
 */
@RestController
@RequestMapping("/jenkins")
@Api(value = "Jenkins管理", tags = "Jenkins管理")
@Slf4j
public class JenkinsController {

    private JenkinsServer jenkinsServer;

    @GetMapping("/connect")
    @ApiOperation(value = "Jenkins连通性检查", tags = "Jenkins管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "registryDetailRequestDto", required = false, value = "查询条件", dataType = "String", paramType = "query"),
    })
    private JsonResult jenkinsConnectCheck() {
        try {
            JenkinsServer connection = JenkinsConnect.connection();
            boolean running = connection.isRunning();
            return running ? JsonResult.success(connection.getVersion().getLiteralVersion(), "在线中") : JsonResult.error("已离线");
        } catch (Exception e) {
            log.error(">>> jenkinsConnectCheck 调用失败: {}", e.getLocalizedMessage(), e);
            return JsonResult.error(e.getMessage());
        }
    }
}
