package com.microservice.controller;

import com.microservice.dto.FlaskCallBackDto;
import com.microservice.protocol.BaseProtocol;
import com.microservice.utils.ProtoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zhangwei
 * @date 2020-10-28
 * <p>
 */
@RestController
@Slf4j
@RequestMapping(value = ProtoUtils.CALL_BACK_ROOT)
@Api(value = "回调处理", tags = "回调处理", description = "回调处理")
public class CallBackController {

    @Autowired
    private ApplicationContext applicationContext;

    @ApiOperation(value = "回调处理API", tags = "回调处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "callBackDto", required = true, value = "回调参数", dataType = "FlaskCallBackDto", paramType = "body"),
    })
    @PostMapping(value = ProtoUtils.CALL_BACK_HANDLER)
    public void handler(@RequestBody FlaskCallBackDto callBackDto) {
        if (null == callBackDto || StringUtils.isAnyBlank(callBackDto.getSeq(), callBackDto.getHandleStatus())) {
            log.error(">>> 回调参数不能为空");
        }
        String seq = callBackDto.getSeq();
        BaseProtocol protocol = ProtoUtils.protocol(seq, applicationContext);
        protocol.process(callBackDto);
    }
}
