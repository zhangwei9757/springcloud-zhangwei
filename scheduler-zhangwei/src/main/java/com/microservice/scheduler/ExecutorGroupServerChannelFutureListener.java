package com.microservice.scheduler;

import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.enums.RaftStatusEnum;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.redis.RedisDefaultGroupServerHandler;
import com.microservice.utils.ApplicationContextUtil;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RedisUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Set;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 */
@Slf4j
public class ExecutorGroupServerChannelFutureListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {

        Channel channel = channelFuture.channel();
        if (!channelFuture.isSuccess()) {
            log.info(">>> server operationComplete 服务器[{}], 启动失败...", channel.localAddress());
        } else {
            log.info(">>> server operationComplete 服务器[{}], 启动成功...", channel.localAddress());

            /*
               ===========================
               ==启动默认开启选举, 程序的入口==
               ===========================
             */
            RedisUtil redisUtil = ApplicationContextUtil.getBean(RedisUtil.class);
            assert redisUtil != null;
            SchedulerConfigurationProperties properties = ApplicationContextUtil.getBean(SchedulerConfigurationProperties.class);
            assert properties != null;
            RedisDefaultClientHandler defaultClientHandler = ApplicationContextUtil.getBean(RedisDefaultClientHandler.class);
            assert defaultClientHandler != null;
            ExecutorGroupServer groupServer = ApplicationContextUtil.getBean(ExecutorGroupServer.class);
            assert groupServer != null;
            RedisDefaultGroupServerHandler defaultGroupServerHandler = ApplicationContextUtil.getBean(RedisDefaultGroupServerHandler.class);
            assert defaultGroupServerHandler != null;

            Set<String> serverAddress = properties.findServerAddress();
            StringBuilder sb = new StringBuilder(">>> 服务器集群列表: ");
            serverAddress.forEach(f -> {
                sb.append("http://").append(f).append(", ");
            });
            log.info(sb.toString());

            RedisListenerEnum[] values = RedisListenerEnum.values();
            StringBuilder sb2 = new StringBuilder(">>> 集群内部订阅主题: ");
            Arrays.stream(values).forEach(f -> {
                sb2.append(f.getType()).append(", ");
            });
            log.info(sb2.toString());
            LocalDateTime now = LocalDateTime.now();

            // 本机 ipv4
            String hostAddress = defaultClientHandler.getHostAddress();

            // 初始化 key
            String voteKey = defaultClientHandler.findVoteKey(properties.getRegisterName());
            String clusterMemberKey = defaultClientHandler.findClusterMemberKey(properties.getRegisterName());
            String balanceKey = defaultClientHandler.findBalanceKey(properties.getRegisterName());
            String heartbeatKey = defaultClientHandler.findClusterHeartbeatKey(properties.getRegisterName());

            Environment environment = ApplicationContextUtil.getBean(Environment.class);
            assert environment != null;
            String port = environment.getProperty("server.port");

            // 生成投票信息
            RedisMessage message = new RedisMessage();
            message
                    .setRegisterTime(now)
                    .setVoteHost(hostAddress)
                    .setHealthIpPort(properties.mergeIpPort(hostAddress, Integer.parseInt(port)))
                    .setHealthUrl(message.mergeHealthUrl(properties.getClusterHealthCheckUrl()))
                    .setCurrentRaftStatus(RaftStatusEnum.FOLLOWER)
                    .setClusterName(properties.getClusterName())
                    .setRegisterName(properties.getRegisterName())
                    .setRegisterPort(properties.getServerPort())
                    .setClusterPrefKey(defaultClientHandler.findClusterPrefKey())
                    .setClusterMemberKey(clusterMemberKey)
                    .setClusterListKey(defaultClientHandler.findClusterListKey())
                    .setVoteKeyPref(defaultClientHandler.findVotePrefKey())
                    .setVoteKey(voteKey)
                    .setBalancePrefKey(defaultClientHandler.findNextBalancePrefKey())
                    .setBalanceKey(balanceKey)
                    .setBalanceNextKey(defaultClientHandler.findNextBalanceKey())
                    .setLeaderKey(defaultClientHandler.findLeaderKey())
                    .setHeartbeatPrefKey(defaultClientHandler.findClusterHeartbeatPrefKey())
                    .setHeartbeatKey(heartbeatKey);

            // 保存相关工具
            groupServer.setChannel(channel);
            groupServer.setCurrentServer(message);
            groupServer.setLastVoteTime(now.toEpochSecond(ZoneOffset.ofHours(8)));

            defaultGroupServerHandler.memberOffLine(message);

            // 初始化集群成员注册信息
            defaultGroupServerHandler.flushClusterRegister(message);
            // 初始化集群列表注信息
            defaultGroupServerHandler.flushClusterList(message, false);

            // 推送服务器已启动通知, 所有在线集群成员开始选举
            String jsonMessage = JsonUtils.toJson(message);
            defaultClientHandler.eventPush(RedisListenerEnum.RAFT, jsonMessage);
        }
    }
}
