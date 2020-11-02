package com.microservice.protocol;

import com.microservice.annotation.lock.CacheLock;
import com.microservice.bean.ExecutorJobMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
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
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 * 注册
 */
@Component
@Slf4j
public class RegisterRequest extends BaseProtocol {

    @Autowired
    private ISchedulerRegistryService registryService;

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheLock(prefix = "RegisterRequest", expire = 60)
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        String content = messageProtocol.getContent();
        ExecutorJobMessage executorJobMessage = JsonUtils.fromJson(content, ExecutorJobMessage.class);
        log.info(">>> 服务器接收到注册事件, 内容: {}", executorJobMessage);

        // 写表，注册执行器: 服务名，注册时间，注册序号
        assert executorJobMessage != null;
        SchedulerConfigurationProperties properties = executorJobMessage.getProperties();
        String host = executorJobMessage.getHost();
        String port = executorJobMessage.getPort();
        String registerName = properties.getRegisterName();


        // 先获取当前是否已经有注册的信息，如果有就添加主机信息，如果没有就创建主机信息
        SchedulerRegistryRequestDto query = new SchedulerRegistryRequestDto();
        query.setRegistryAppName(registerName);
        List<SchedulerRegistryRequestDto> registries = registryService.findRegistryByAppName(query);

        LocalDateTime now = LocalDateTime.now();

        if (!CollectionUtils.isEmpty(registries)) {
            // 存在数据，修改数据即可
            SchedulerRegistryRequestDto schedulerRegistry = registries.get(0);
            List<SchedulerRegistryDetailRequestDto> registryDetails = schedulerRegistry.getRegistryDetails();

            boolean mustInsert = true;
            SchedulerRegistryDetailRequestDto max = new SchedulerRegistryDetailRequestDto().setRegisterDetailSort(0);

            if (!CollectionUtils.isEmpty(registryDetails)) {
                // 找出除了自己以外的，sort 最大值
                max = registryDetails.parallelStream()
                        .filter(f -> Objects.deepEquals(f.getRegisterDetailAppName(), registerName))
                        .max(Comparator.comparing(SchedulerRegistryDetailRequestDto::getRegisterDetailSort))
                        .orElseGet(() -> new SchedulerRegistryDetailRequestDto().setRegisterDetailSort(0));

                // 不为空表示，里面可能有自己的注册信息
                SchedulerRegistryDetailRequestDto detail = registryDetails.parallelStream()
                        .filter(f -> Objects.deepEquals(f.getRegisterDetailAppName(), registerName)
                                && Objects.deepEquals(f.getRegisterDetailIp(), host)
                                && Objects.deepEquals(f.getRegisterDetailPort(), port))
                        .findFirst()
                        .orElseGet(() -> null);

                if (Objects.nonNull(detail)) {
                    // 有注册详细信息, 修改注册信息
                    String status = detail.getRegisterDetailStatus();
                    if (Objects.deepEquals(status, Constants.OFF_LINE)) {
                        // 如果离线修改状态即可
                        detail.setRegisterDetailStatus(Constants.ON_LINE)
                                .setRegisterDetailOnlineTime(now)
                                .setRegisterDetailOfflineTime(null)
                        ;
                        // 修改状态
                        boolean saveOrUpdate = this.registryDetailService.saveOrUpdate(detail);
                        if (!saveOrUpdate) {
                            throw new SchedulerCoreException(ErrCode.REGISTER_FAIL);
                        }
                        mustInsert = false;
                    } else {
                        // 如果已经在线了，打印日志记录即可
                        log.info(">>> host: {}, port: {}, 当前状态: {}",
                                host, port, Objects.deepEquals(status, Constants.OFF_LINE) ? "离线" : "在线");
                        mustInsert = false;
                    }
                }
                // 无注册信息，添加注册信息
            }
            // 为空表示，里面没有自己的注册信息, 继续执行即可

            if (mustInsert) {
                SchedulerRegistryDetailRequestDto registryDetailRequest = new SchedulerRegistryDetailRequestDto();
                registryDetailRequest.setRegisterDetailAppName(registerName)
                        .setRegisterDetailRegistryId(schedulerRegistry.getRegistryId())
                        .setRegisterDetailIp(host)
                        .setRegisterDetailPort(port)
                        .setRegisterDetailSort(max.getRegisterDetailSort() + 1)
                        .setRegisterDetailStatus(Constants.ON_LINE)
                        .setRegisterDetailOnlineTime(now)
                        .setRegisterDetailCreateTime(now)
                ;
                // 添加注册信息
                boolean saveOrUpdate = this.registryDetailService.saveOrUpdate(registryDetailRequest);
                if (!saveOrUpdate) {
                    throw new SchedulerCoreException(ErrCode.REGISTER_FAIL);
                }
            }
        } else {
            // 不存在数据，添加数据即可
            SchedulerRegistryRequestDto registry = new SchedulerRegistryRequestDto();
            registry.setRegistryAppName(registerName)
                    .setRegistryCreateTime(now)
                    .setRegistryDesc(registerName)
                    .setRegistryDetails(null)
            ;
            // 添加注册信息
            boolean save = this.registryService.saveOrUpdate(registry);
            if (!save) {
                throw new SchedulerCoreException(ErrCode.REGISTER_FAIL);
            }

            List<SchedulerRegistryRequestDto> list = registryService.findRegistryByAppName(registry);
            SchedulerRegistryRequestDto registryRequestDto = list.get(0);

            SchedulerRegistryDetailRequestDto registryDetailRequest = new SchedulerRegistryDetailRequestDto();
            registryDetailRequest.setRegisterDetailAppName(registerName)
                    .setRegisterDetailRegistryId(registryRequestDto.getRegistryId())
                    .setRegisterDetailIp(host)
                    .setRegisterDetailPort(port)
                    .setRegisterDetailSort(1)
                    .setRegisterDetailStatus(Constants.ON_LINE)
                    .setRegisterDetailOnlineTime(now)
                    .setRegisterDetailCreateTime(now)
            ;
            // 添加注册节点信息
            boolean saveOrUpdate = this.registryDetailService.saveOrUpdate(registryDetailRequest);
            if (!saveOrUpdate) {
                throw new SchedulerCoreException(ErrCode.REGISTER_FAIL);
            }
        }

        log.info(">>> 服务器接收到注册事件, 执行器: {}, 注册成功!", executorJobMessage.getAppName());
        ExecutorGroupServerHandler.ALL_CHANNELS.put(registerName, channel);
        ExecutorGroupServerHandler.CHANNEL_GROUP.add(channel);
    }
}
