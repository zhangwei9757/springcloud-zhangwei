package com.microservice.listener;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.enums.RaftStatusEnum;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.server.ExecutorGroupServer;
import com.microservice.server.SchedulerActuatorScanningHandler;
import com.microservice.service.ISchedulerTaskCronService;
import com.microservice.service.ISchedulerTaskGivenService;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.Constants;
import com.microservice.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p> 集群心跳监管处理
 */
@Slf4j
public class RedisClusterRefreshListener implements MessageListener {

    private RedisDefaultClientHandler defaultClientHandler;
    private RedisDefaultGroupServerHandler defaultGroupServerHandler;
    private SchedulerConfigurationProperties properties;
    private ExecutorGroupServer groupServer;
    private ScheduledFuture<?> heartbeatSchedule;
    private ScheduledFuture<?> taskCronScanningSchedule;
    private ScheduledFuture<?> taskGivenScanningSchedule;
    private ScheduledFuture<?> autoActuatorScanningSchedule;
    private Executor executor;
    private ISchedulerTaskCronService taskCronService;
    private ISchedulerTaskGivenService taskGivenService;
    private SchedulerActuatorScanningHandler actuatorScanningHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
        defaultGroupServerHandler = ApplicationContextUtil.getBean(RedisDefaultGroupServerHandler.class);
        properties = ApplicationContextUtil.getBean(SchedulerConfigurationProperties.class);
        groupServer = ApplicationContextUtil.getBean(ExecutorGroupServer.class);
        executor = ApplicationContextUtil.APPLICATION_CONTEXT.getBean("executor", Executor.class);
        taskCronService = ApplicationContextUtil.getBean(ISchedulerTaskCronService.class);
        taskGivenService = ApplicationContextUtil.getBean(ISchedulerTaskGivenService.class);
        actuatorScanningHandler = ApplicationContextUtil.getBean(SchedulerActuatorScanningHandler.class);

        assert groupServer != null;
        RedisMessage currentServer = groupServer.getCurrentServer();
        RedisMessage leader = defaultClientHandler.findLeader();
        groupServer.setLeader(leader);

        if (Objects.isNull(leader)) {
            defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
            if (Objects.nonNull(heartbeatSchedule)) {
                heartbeatSchedule.cancel(true);
            }
            if (Objects.nonNull(taskCronScanningSchedule)) {
                taskCronScanningSchedule.cancel(true);
            }
            if (Objects.nonNull(taskGivenScanningSchedule)) {
                taskGivenScanningSchedule.cancel(true);
            }
            if (Objects.nonNull(autoActuatorScanningSchedule)) {
                autoActuatorScanningSchedule.cancel(true);
            }
            return;
        }

        // 只有集群 Leader 才可以受理集群刷新管理事件
        if (Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort())) {
            if (Objects.nonNull(heartbeatSchedule)) {
                heartbeatSchedule.cancel(true);
            }
            if (Objects.nonNull(taskCronScanningSchedule)) {
                taskCronScanningSchedule.cancel(true);
            }
            if (Objects.nonNull(taskGivenScanningSchedule)) {
                taskGivenScanningSchedule.cancel(true);
            }
            if (Objects.nonNull(autoActuatorScanningSchedule)) {
                autoActuatorScanningSchedule.cancel(true);
            }
            this.clusterHeartbeatManager();

            // 一旦进入集群 Leader 状态，开启任务扫描
            executor.execute(this::taskCronScanning);
            executor.execute(this::taskGivenScanning);
            executor.execute(this::autoActuatorScanning);
        }
    }

    /**
     * Leader 进入集群刷新管理中
     */
    private void clusterHeartbeatManager() {
        RedisMessage currentServer = groupServer.getCurrentServer();
        Channel channel = groupServer.getChannel();

        heartbeatSchedule = channel.eventLoop().schedule(() -> {
            if (properties.isAllowLog()) {
                log.info("");
                log.info(">>> ----------------------------clusterHeartbeatManager start-------------------------------");
            }

            RedisMessage leader = defaultClientHandler.findLeader();
            if (Objects.isNull(leader)) {
                if (properties.isAllowLog()) {
                    log.error(">>> {}, 未发现 Leader 信息...", currentServer.ipMappingPort());
                }
                currentServer.setCurrentRaftStatus(RaftStatusEnum.FOLLOWER);
                defaultClientHandler.updateMessageByKey(currentServer.getVoteKey(), JsonUtils.toJson(currentServer));
                defaultClientHandler.eventPush(RedisListenerEnum.RAFT, JsonUtils.toJson(currentServer));
                return;
            }
            // 1. 无论自己注册信息是否存在，必须保存自己的在线信息一致性
            defaultGroupServerHandler.flushClusterRegister(currentServer);

            // 2. leader 刷新管理中，要同步检查集群成员健康状态, 一旦健康检查失败，直接清除成员信息
            boolean isLeader = Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort());
            defaultGroupServerHandler.clusterHealthCheck(currentServer, isLeader, leader);

            // 3. 通过心跳记录刷新所有记录
            //defaultGroupServerHandler.offLineClusterList(currentServer, leader);

            // 4. clusterList 刷新
            defaultGroupServerHandler.flushClusterList(currentServer, false);

            // 5. 准备负载均衡服务器下次给客户端连接
            log.info(">>> {} => {}, 刷新服务器列表与负载均衡...", currentServer.ipMappingPort(), currentServer.getCurrentRaftStatus());
            defaultGroupServerHandler.clusterLoadBalance(currentServer, leader);

            // 6. 集群要负责通知成员心跳
            //defaultClientHandler.eventPush(RedisListenerEnum.CLUSTER_MEMBER_HEARTBEAT, JsonUtils.toJson(currentServer));

            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------clusterHeartbeatManager end-------------------------------");
            }

            // 6. 再次进入集群刷新管理中
            this.clusterHeartbeatManager();

        }, properties.getClusterHeartbeat(), TimeUnit.SECONDS);
    }

    /**
     * 集群表达式任务扫描
     */
    private void taskCronScanning() {
        Channel channel = groupServer.getChannel();

        taskCronScanningSchedule = channel.eventLoop().schedule(() -> {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>  taskCronScanning");
            // 1. 获取所有任务
            List<SchedulerTaskCronRequestDto> allTask = taskCronService.tasks();

            // 2. 获取所有已开启的任务
            Set<SchedulerTaskCronRequestDto> tasks = allTask.parallelStream()
                    .filter(f -> Objects.deepEquals(f.getTaskCronStatus(), Constants.RUNNING))
                    .collect(Collectors.toSet());

            // 3. 执行任务
            defaultGroupServerHandler.scanningCronSchedulerTasks(tasks);

            // 4. 再次任务扫描
            this.taskCronScanning();

        }, properties.getClusterCronTaskScanningCycle(), TimeUnit.SECONDS);
    }

    /**
     * 集群指定时间任务扫描
     */
    private void taskGivenScanning() {
        Channel channel = groupServer.getChannel();

        taskGivenScanningSchedule = channel.eventLoop().schedule(() -> {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>  taskGivenScanning");
            // 1. 获取所有任务
            List<SchedulerTaskGivenRequestDto> allTask = taskGivenService.tasks();

            // 2. 获取所有已开启的任务
            Set<SchedulerTaskGivenRequestDto> tasks = allTask.parallelStream()
                    .filter(f -> Objects.deepEquals(f.getTaskGivenStatus(), Constants.RUNNING))
                    .collect(Collectors.toSet());

            // 3. 执行任务
            defaultGroupServerHandler.scanningGivenSchedulerTasks(tasks, channel);

            // 4. 再次任务扫描
            this.taskGivenScanning();

        }, properties.getClusterGivenTaskScanningCycle(), TimeUnit.SECONDS);
    }


    /**
     * 自动扫描执行状态阶段
     */
    private void autoActuatorScanning() {
        Channel channel = groupServer.getChannel();

        autoActuatorScanningSchedule = channel.eventLoop().schedule(() -> {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>  autoActuatorScanning");
            actuatorScanningHandler.autoActuatorScanning();
            this.autoActuatorScanning();
        }, properties.getClusterActuatorStatusScanningCycle(), TimeUnit.SECONDS);
    }
}