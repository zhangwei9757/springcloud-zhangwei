package com.microservice.controller;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerServerRegister;
import com.microservice.dto.ResponseDto;
import com.microservice.exception.SchedulerCoreException;
import com.microservice.redis.RedisDefaultClientHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Api(value = "scheduler", tags = "scheduler")
public class SchedulerClientController {

    @Resource
    private RedisDefaultClientHandler clientManager;

    @Resource
    private SchedulerConfigurationProperties properties;

    @ApiOperation(value = "查询集群服务列表", tags = "scheduler")
    @GetMapping(value = "/cluster/list")
    public ResponseDto list() throws SchedulerCoreException {
        List<SchedulerServerRegister> clusterList = clientManager.findClusterList();
        return ResponseDto.success(clusterList);
    }

    @ApiOperation(value = "查询集群服务列表成员状态", tags = "scheduler")
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
