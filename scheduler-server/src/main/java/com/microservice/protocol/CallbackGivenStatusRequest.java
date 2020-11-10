package com.microservice.protocol;

import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerTaskMessage;
import com.microservice.client.BaseProtocol;
import com.microservice.dto.SchedulerRegistryDetailRequestDto;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.entity.SchedulerTaskLog;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.service.ISchedulerTaskGivenService;
import com.microservice.service.ISchedulerTaskLogService;
import com.microservice.utils.Constants;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zw
 * @date 2020-10-19
 * <p> 指定时间任务回写执行状态
 */
@Component
@Slf4j
public class CallbackGivenStatusRequest extends BaseProtocol {

    @Autowired
    private ISchedulerTaskGivenService taskGivenService;

    @Autowired
    private ISchedulerTaskLogService taskLogService;

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Override
    public void onProcess(MessageProtocolPoJo.MessageProtocol messageProtocol, Channel channel) {
        // 自动回写
        autoCallbackWirte(messageProtocol);
    }

    /**
     * 自动回写
     *
     * @param messageProtocol
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoCallbackWirte(MessageProtocolPoJo.MessageProtocol messageProtocol) {
        String content = messageProtocol.getContent();
        SchedulerTaskMessage task = JsonUtils.fromJson(content, SchedulerTaskMessage.class);
        SchedulerTaskGivenRequestDto givenRequest = task.getTaskGivenDto();

        if (Objects.nonNull(givenRequest)) {
            log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 回写任务信息: {}", givenRequest);

            String actuatorKey = task.getActuatorKey();
            String jobHandler = task.getJobHandler();
            LocalDateTime schedulerGivenTime = task.getSchedulerGivenTime();
            String executorAddress = task.getExecutorAddress();
            String jobHandlerParam = task.getJobHandlerParam();

            Integer taskGivenId = givenRequest.getTaskGivenId();
            int code = givenRequest.getCode();
            // 执行相关结果
            String msg = givenRequest.getMsg();
            String content1 = givenRequest.getContent();
            Integer retryCount = givenRequest.getTaskGivenRetryCount();
            LocalDateTime executeTime = givenRequest.getExecuteTime();

            // 调度相关
            LocalDateTime triggerTime = givenRequest.getTriggerTime();
            int triggerCode = givenRequest.getTriggerCode();
            String triggerMsg = givenRequest.getTriggerMsg();

            if (code == HttpStatus.OK.value()) {
                // 成功回写
                SchedulerTaskGivenRequestDto dto = new SchedulerTaskGivenRequestDto();
                dto.setTaskGivenId(taskGivenId);
                dto.setTaskGivenExecuteStatus(Constants.TASK_ALREADY_EXECUTE);
                boolean update = taskGivenService.updateGivenExecuteStatus(dto);
                log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 回写结果: {}", update);
            } else {
                // 失败回写
                SchedulerTaskGivenRequestDto dto = new SchedulerTaskGivenRequestDto();
                dto.setTaskGivenId(taskGivenId);
                dto.setTaskGivenExecuteStatus(Constants.TASK_WAIT_RETRY);
                dto.setTaskGivenLastExecuteTime(LocalDateTime.now());
                dto.setTaskGivenRetryCount(Objects.isNull(retryCount) ? 1 : retryCount + 1);

                boolean update = taskGivenService.updateGivenExecuteStatus(dto);
                log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 回写结果: {}", update);
            }

            // 无论执行多少次，必须保存每次执行日志记录下来
            String taskGivenWriteLog = givenRequest.getTaskGivenWriteLog();
            if (Objects.deepEquals(taskGivenWriteLog, Constants.RUNNING)) {
                SchedulerTaskLogRequestDto taskLogRequest = new SchedulerTaskLogRequestDto();

                String[] ipAndPort = SchedulerConfigurationProperties.splitIpPort(executorAddress);
                SchedulerRegistryDetailRequestDto query = new SchedulerRegistryDetailRequestDto();
                query.setRegisterDetailAppName(actuatorKey)
                        .setRegisterDetailIp(ipAndPort[0])
                        .setRegisterDetailPort(ipAndPort[1]);
                SchedulerRegistryDetailRequestDto detail = registryDetailService.findRegistryDetailByAppNameAndIpAndPort(query);
                if (Objects.nonNull(detail)) {
                    taskLogRequest.setLogRegistryDetailId(detail.getRegisterDetailId())
                            .setLogTaskId(taskGivenId)
                            .setLogExecutorAddress(executorAddress)
                            .setLogExecutorHandler(jobHandler)
                            .setLogExecutorParam(jobHandlerParam)
                            .setLogTriggerTime(triggerTime)
                            .setLogTriggerCode(String.valueOf(triggerCode))
                            .setLogTriggerMsg(triggerMsg)
                            .setLogHandleTime(executeTime)
                            .setLogHandleCode(String.valueOf(code))
                            .setLogHandleMsg(msg)
                            .setLogTaskType(Constants.TASK_TYPE_OF_GIVEN)
                            .setLogCreateTime(LocalDateTime.now())
                    ;

                    SchedulerTaskLog logEntity = new SchedulerTaskLog();
                    BeanUtils.copyProperties(taskLogRequest, logEntity);
                    boolean save = taskLogService.save(logEntity);
                    log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 调度日志添加结果: {}", save);
                } else {
                    log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 调度日志添加结果: 失败!未发现{}执行器注册节点信息.");
                }
            }
        }

        SchedulerTaskCronRequestDto taskCronDto = task.getTaskCronDto();
        if (Objects.nonNull(taskCronDto)) {
            {
                log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 回写任务信息: {}", taskCronDto);

                String actuatorKey = task.getActuatorKey();
                String jobHandler = task.getJobHandler();

                String executorAddress = task.getExecutorAddress();
                String jobHandlerParam = task.getJobHandlerParam();

                Integer taskGivenId = taskCronDto.getTaskCronId();
                int code = taskCronDto.getCode();
                // 执行相关结果
                String msg = taskCronDto.getMsg();
                String content1 = taskCronDto.getContent();
                LocalDateTime executeTime = taskCronDto.getExecuteTime();

                // 调度相关
                LocalDateTime triggerTime = taskCronDto.getTriggerTime();
                int triggerCode = taskCronDto.getTriggerCode();
                String triggerMsg = taskCronDto.getTriggerMsg();

                // 无论执行多少次，必须保存每次执行日志记录下来
                String taskCronWriteLog = taskCronDto.getTaskCronWriteLog();
                if (Objects.deepEquals(taskCronWriteLog, Constants.RUNNING)) {
                    SchedulerTaskLogRequestDto taskLogRequest = new SchedulerTaskLogRequestDto();

                    String[] ipAndPort = SchedulerConfigurationProperties.splitIpPort(executorAddress);
                    SchedulerRegistryDetailRequestDto query = new SchedulerRegistryDetailRequestDto();
                    query.setRegisterDetailAppName(actuatorKey)
                            .setRegisterDetailIp(ipAndPort[0])
                            .setRegisterDetailPort(ipAndPort[1]);
                    SchedulerRegistryDetailRequestDto detail = registryDetailService.findRegistryDetailByAppNameAndIpAndPort(query);
                    if (Objects.nonNull(detail)) {
                        taskLogRequest.setLogRegistryDetailId(detail.getRegisterDetailId())
                                .setLogTaskId(taskGivenId)
                                .setLogExecutorAddress(executorAddress)
                                .setLogExecutorHandler(jobHandler)
                                .setLogExecutorParam(jobHandlerParam)
                                .setLogTriggerTime(triggerTime)
                                .setLogTriggerCode(String.valueOf(triggerCode))
                                .setLogTriggerMsg(triggerMsg)
                                .setLogHandleTime(executeTime)
                                .setLogHandleCode(String.valueOf(code))
                                .setLogHandleMsg(msg)
                                .setLogTaskType(Constants.TASK_TYPE_OF_CRON)
                                .setLogCreateTime(LocalDateTime.now())
                        ;

                        SchedulerTaskLog logEntity = new SchedulerTaskLog();
                        BeanUtils.copyProperties(taskLogRequest, logEntity);
                        boolean save = taskLogService.save(logEntity);
                        log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 调度日志添加结果: {}", save);
                    } else {
                        log.info(">>> 服务器接收到指定时间任务回写执行状态事件, 调度日志添加结果: 失败!未发现{}执行器注册节点信息.");
                    }
                }
            }
        }
    }
}
