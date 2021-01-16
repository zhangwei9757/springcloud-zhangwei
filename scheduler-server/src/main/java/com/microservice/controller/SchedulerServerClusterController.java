package com.microservice.controller;

import com.microservice.bean.ActuatorHealthCheckResult;
import com.microservice.bean.SchedulerServerRegister;
import com.microservice.dto.ResponseDto;
import com.microservice.enums.HostStatusEnum;
import com.microservice.exception.SchedulerCoreException;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.utils.ErrCode;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RestTemplateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zw
 * @date 2020-10-02
 * <p> scheduler  client
 */
@RestController
@Slf4j
@RequestMapping(value = "/v1")
@Api(value = "V1", tags = "V1")
public class SchedulerServerClusterController {

    @Resource
    private RedisDefaultClientHandler clientManager;

    @GetMapping(value = {"/ping", "/"})
    @ApiOperation(value = "PING", tags = "V1")
    @ResponseBody
    public String ping() {
        return "Welcome to scheduler server...";
    }

    @PostMapping(value = "/healthCheck")
    @ApiOperation(value = "集群成员心跳检查", tags = "V1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ipPort", required = true, value = "ip:port", dataType = "String", paramType = "query"),
    })
    @ResponseBody
    public ResponseDto healthCheck(@RequestParam("ipPort") String ipPort) {
        try {
            if (StringUtils.isBlank(ipPort)) {
                return ResponseDto.error(ErrCode.PARAMS_EMPTY);
            }
            ResponseEntity<String> responseEntity = RestTemplateUtils.get("http://" + ipPort + "/actuator/health", String.class);
            String body = responseEntity.getBody();
            ActuatorHealthCheckResult checkResult = JsonUtils.fromJson(body, ActuatorHealthCheckResult.class);
            return ResponseDto.success(checkResult);
        } catch (Exception e) {
            ActuatorHealthCheckResult checkResult = new ActuatorHealthCheckResult();
            checkResult.setStatus(HostStatusEnum.UNKNOWN.name());
            return ResponseDto.success(checkResult);
        }
    }

    @ApiOperation(value = "查询集群服务列表", tags = "V1")
    @GetMapping(value = "/cluster/list")
    public ResponseDto list() throws SchedulerCoreException {
        List<SchedulerServerRegister> clusterList = clientManager.findClusterList();
        return ResponseDto.success(clusterList);
    }

    @ApiOperation(value = "查询集群服务列表成员状态", tags = "V1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverAddress", required = true, value = "集群成员IP", dataType = "String", paramType = "path"),
    })
    @GetMapping(value = "/cluster/{serverAddress}")
    public ResponseDto serverAddress(@PathVariable("serverAddress") String serverAddress) throws SchedulerCoreException {
        List<SchedulerServerRegister> clusterList = clientManager.findClusterList();
        SchedulerServerRegister serverRegister = clusterList.parallelStream()
                .filter(f -> f.getServerIp().equals(serverAddress))
                .findFirst()
                .orElse(null);
        return ResponseDto.success(serverRegister);
    }
}
