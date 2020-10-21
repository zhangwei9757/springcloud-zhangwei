package com.microservice.protocol;

import com.microservice.client.BaseProtocol;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.dto.SchedulerRegistryRequestDto;
import com.microservice.exception.SchedulerCoreException;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.server.ExecutorGroupServerHandler;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.service.ISchedulerRegistryService;
import com.microservice.utils.Constants;
import com.microservice.utils.ErrCode;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 * 注销
 */
@Component
@Slf4j
public class DeregisterRequest extends BaseProtocol {

    @Autowired
    private ISchedulerRegistryService registryService;

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {


        String registerAppName = ExecutorGroupServerHandler.findName(channel);

        LocalDateTime now = LocalDateTime.now();
        {

            // 当集群成员与客户端断开连接时，需要写表记录已下线 [客户端下线]
            log.info(">>> 服务器接收到注销事件, 注销执行器: {}", registerAppName);

            SchedulerRegistryRequestDto query = new SchedulerRegistryRequestDto();
            query.setRegistryAppName(registerAppName);
            List<SchedulerRegistryRequestDto> registries = registryService.findRegistryByAppName(query);

            if (!CollectionUtils.isEmpty(registries)) {
                // 如果已存在注册信息，要获取真实节点信息
                SchedulerRegistryRequestDto registry = registries.get(0);
                List<SchedulerRegistryDetailRequestDto> registryDetails = registry.getRegistryDetails();

                // 如果存在节点列表信息，要获取真实注销客户端信息
                if (!CollectionUtils.isEmpty(registryDetails)) {
                    SchedulerRegistryDetailRequestDto detail = registryDetails.parallelStream()
                            .filter(f -> Objects.deepEquals(f.getRegisterDetailAppName(), registerAppName))
                            .findFirst().orElseGet(() -> null);

                    if (Objects.nonNull(detail)) {
                        // 存在客户端原注册信息，直接修改当前客户端状态为： 注销
                        detail.setRegisterDetailOfflineTime(now)
                                .setRegisterDetailStatus(Constants.OFF_LINE)
                        ;
                        boolean update = registryDetailService.saveOrUpdate(detail);
                        if (!update) {
                            throw new SchedulerCoreException(ErrCode.DEREGISTER_FAIL);
                        }
                    }
                }
            }
        }

        ExecutorGroupServerHandler.ALL_CHANNELS.remove(registerAppName);
        ExecutorGroupServerHandler.CHANNEL_GROUP.remove(channel);

        log.info(">>> 服务器接收到注销事件, 执行器: {}, {} 注销成功!", registerAppName, now);
    }
}
