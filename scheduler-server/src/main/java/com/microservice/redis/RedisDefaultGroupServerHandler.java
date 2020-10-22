package com.microservice.redis;

import com.microservice.annotation.lock.CacheLock;
import com.microservice.bean.*;
import com.microservice.cron.CronExpression;
import com.microservice.dto.SchedulerTaskCronRequestDto;
import com.microservice.dto.SchedulerTaskGivenRequestDto;
import com.microservice.dto.SchedulerTaskLogRequestDto;
import com.microservice.entity.SchedulerRegistryDetail;
import com.microservice.entity.SchedulerTaskLog;
import com.microservice.enums.HostStatusEnum;
import com.microservice.enums.RaftStatusEnum;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.server.ExecutorGroupServer;
import com.microservice.service.ISchedulerRegistryDetailService;
import com.microservice.service.ISchedulerTaskGivenService;
import com.microservice.service.ISchedulerTaskLogService;
import com.microservice.utils.Constants;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RedisUtil;
import com.microservice.utils.RestTemplateUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zw
 * @date 2020-10-02
 * <p> 集群管理
 */
@Service
@Slf4j
public class RedisDefaultGroupServerHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ExecutorGroupServer groupServer;

    @Resource
    private SchedulerConfigurationProperties properties;

    @Resource
    private RedisDefaultClientHandler defaultClientHandler;

    @Resource
    private ISchedulerTaskGivenService taskGivenService;

    @Autowired
    private ISchedulerRegistryDetailService registryDetailService;

    @Autowired
    private ISchedulerTaskLogService taskLogService;

    /**
     * 检查指定服务器健康指标
     *
     * @param url
     * @return
     */
    public ActuatorHealthCheckResult findActuatorHealthCheckResult(String url) {
        ActuatorHealthCheckResult checkResult;
        try {
            ResponseEntity<String> responseEntity = RestTemplateUtils.get(url, String.class);
            String body = responseEntity.getBody();
            checkResult = JsonUtils.fromJson(body, ActuatorHealthCheckResult.class);
        } catch (Exception e) {
            checkResult = new ActuatorHealthCheckResult(HostStatusEnum.DOWN.getType());
        }
        return checkResult;
    }

    /**
     * 集群健康检查
     *
     * @param currentServer 当前集群成员
     * @param isLeader      是否是 leader
     * @param leader        上次leader信息
     */
    public boolean clusterHealthCheck(RedisMessage currentServer, boolean isLeader, RedisMessage leader) {
        boolean rtn = true;

        Set<RedisMessage> votes = defaultClientHandler.findVotes(currentServer.getVoteKeyPref());
        votes = votes.parallelStream()
                .filter(f -> !Objects.deepEquals(leader.ipMappingPort(), f.ipMappingPort()))
                .collect(Collectors.toSet());

        if (isLeader) {
            // 1. 当前是 Leader, 检查集群成员健康状态
            if (!CollectionUtils.isEmpty(votes)) {
                for (RedisMessage vote : votes) {
                    // 必须是完全启动后，对方同意检查才可以
                    if (vote.isEnableHealthCheck()) {
                        String healthUrl = vote.getHealthUrl();
                        ActuatorHealthCheckResult checkResult = this.findActuatorHealthCheckResult(healthUrl);

                        if (properties.isAllowLog()) {
                            log.info(">>> 集群成员健康检查: {} => {}", healthUrl, checkResult.getStatus());
                        }
                        if (!Objects.deepEquals(HostStatusEnum.UP.getType(), checkResult.getStatus())) {
                            // 表示 成员 下线，要重新选举, 但考虑，成员还未完全启动，禁止扫描
                            this.memberOffLine(vote);
                            rtn = false;
                        }
                    }
                }
            }
        } else {
            // 2. 当前是 集群成员, 检查 Leader
            if (Objects.nonNull(leader)) {
                if (leader.isEnableHealthCheck()) {
                    // 必须是leader 同意检查
                    String healthUrl = leader.getHealthUrl();
                    ActuatorHealthCheckResult checkResult = this.findActuatorHealthCheckResult(healthUrl);

                    if (properties.isAllowLog()) {
                        log.info(">>> 集群Leader健康检查: {} => {}", healthUrl, checkResult.getStatus());
                    }

                    if (!Objects.deepEquals(HostStatusEnum.UP.getType(), checkResult.getStatus())) {
                        // 表示 leader 下线，要重新选举
                        this.leaderOffLine();
                        rtn = false;
                    }
                }
            }
        }

        return rtn;
    }

    /**
     * leader 离线，清除相关数据
     */
    public void leaderOffLine() {
        RedisMessage leader = defaultClientHandler.findLeader();
        if (Objects.nonNull(leader)) {
            // 清除投票数据
            defaultClientHandler.removeByKey(leader.getVoteKey());
            // 清除leader
            defaultClientHandler.removeByKey(leader.getLeaderKey());
            defaultClientHandler.removeByKey(leader.getBalanceKey());
            // 清除负载均衡数据
            defaultClientHandler.removeByKey(leader.getBalanceNextKey());
            // 清除负载记录数据
            defaultClientHandler.removeByKey(leader.getClusterMemberKey());
        }
    }

    /**
     * 指定成员 离线，清除相关数据
     */
    public void memberOffLine(RedisMessage member) {
        if (Objects.nonNull(member)) {
            // 清除投票数据
            defaultClientHandler.removeByKey(member.getVoteKey());
            // 清除负载均衡数据
            defaultClientHandler.removeByKey(member.getBalanceNextKey());
            defaultClientHandler.removeByKey(member.getBalanceKey());
            // 清除负载记录数据
            defaultClientHandler.removeByKey(member.getClusterMemberKey());
        }
    }

    /**
     * 刷新集群成员注册信息, [此时扫描，不存在的一率表示未知]
     *
     * @param message
     */
    public void flushClusterRegister(RedisMessage message) {
        // 当前注册信息是否已存在
        List<SchedulerServerRegister> clusterList = defaultClientHandler.findClusterList();

        // 如果不存在，自动同步注册列表数据
        if (CollectionUtils.isEmpty(clusterList)) {
            clusterList = properties.findServerAddress()
                    .parallelStream()
                    .map(m -> new SchedulerServerRegister()
                            .setServerIp(properties.findIpPort(m)[0])
                            .setServerPort(Integer.parseInt(properties.findIpPort(m)[1]))
                            .setRegisterTime(null)
                            .setDeRegisterTime(null)
                            .setStatus(HostStatusEnum.UNKNOWN.getType())
                    )
                    .distinct()
                    .collect(Collectors.toList());
        }

        Set<RedisMessage> votes = defaultClientHandler.findVotes(message.getVoteKeyPref());
        // 如果获取不到表示首次注册
        if (CollectionUtils.isEmpty(votes)) {
            votes.add(message);
        }

        // 使用投票信息，自动同步注册信息，注册列表信息
        clusterList.parallelStream().forEach(register -> {
            RedisMessage realVote = votes.parallelStream()
                    .filter(vote -> Objects.deepEquals(vote.ipMappingPort(),
                            properties.mergeIpPort(register.getServerIp(), register.getServerPort())))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(realVote) && register.ipMappingPort().equals(realVote.ipMappingPort())) {
                register.setRegisterTime(realVote.getRegisterTime())
                        .setDeRegisterTime(null)
                        .setStatus(HostStatusEnum.UP.getType());
            }
        });

        RedisMessage temp = votes.parallelStream()
                .filter(f -> Objects.deepEquals(f.ipMappingPort(), message.ipMappingPort()))
                .findFirst()
                .orElse(message);

        SchedulerServerRegister register = clusterList.parallelStream()
                .filter(f -> Objects.deepEquals(f.ipMappingPort(), message.ipMappingPort()))
                .findFirst()
                .orElse(new SchedulerServerRegister(
                        message.getVoteHost(),
                        message.getRegisterPort(),
                        temp.getRegisterTime(),
                        null,
                        HostStatusEnum.UP.getType()));

        boolean deepEquals = !Objects.deepEquals(register.getStatus(), HostStatusEnum.UP.getType());
        if (!deepEquals) {
            register.setStatus(HostStatusEnum.UP.getType());
        }

        // 同步注册信息
        redisUtil.set(message.getClusterMemberKey(), JsonUtils.toJson(register));
        // 同步列表信息
        redisUtil.set(message.getClusterListKey(), JsonUtils.toJson(clusterList));
        // 同步投票信息
        redisUtil.set(message.getVoteKey(), JsonUtils.toJson(message));
    }

    /**
     * 刷新集群列表信息 [此时扫描，不存在的一率表示下线]
     *
     * @param message
     * @param destroy
     */
    @CacheLock(prefix = "flushClusterList", expire = 60)
    public synchronized void flushClusterList(RedisMessage message, boolean destroy) {

        LocalDateTime now = LocalDateTime.now();


        List<SchedulerServerRegister> clusterList = defaultClientHandler.findClusterList();

        if (CollectionUtils.isEmpty(clusterList)) {
            // 1. 如果不存在，自动同步注册列表数据
            clusterList = properties.findServerAddress()
                    .parallelStream()
                    .map(m -> new SchedulerServerRegister()
                            .setServerIp(properties.findIpPort(m)[0])
                            .setServerPort(Integer.parseInt(properties.findIpPort(m)[1]))
                            .setRegisterTime(null)
                            .setDeRegisterTime(now)
                            .setStatus(HostStatusEnum.DOWN.getType())
                    )
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 2. 投票列表信息
        Set<RedisMessage> votes = defaultClientHandler.findVotes(message.getVoteKeyPref());

        // 3. 使用投票信息，自动同步 注册列表信息
        clusterList.parallelStream().forEach(register -> {
            RedisMessage realVote = votes.parallelStream()
                    .filter(vote -> Objects.deepEquals(vote.ipMappingPort(),
                            properties.mergeIpPort(register.getServerIp(), register.getServerPort())))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(realVote)) {
                register.setRegisterTime(realVote.getRegisterTime())
                        .setDeRegisterTime(null)
                        .setStatus(HostStatusEnum.UP.getType());
            } else {
                LocalDateTime deRegisterTime = register.getDeRegisterTime();
                register.setDeRegisterTime(Objects.nonNull(deRegisterTime) ? deRegisterTime : now)
                        .setStatus(HostStatusEnum.DOWN.getType());
            }
        });

        // 如果自己申请消毁，需要强制消毁列表中自己的信息
        clusterList.parallelStream().forEach(f -> {
            if (Objects.deepEquals(f.ipMappingPort(), message.ipMappingPort()) && destroy) {
                // 表示自己要消毁
                f.setDeRegisterTime(now);
                f.setStatus(HostStatusEnum.DOWN.getType());
            }
        });

        Set<String> serverAddress = properties.findServerAddress();

        // 4. 使用投票信息，自动同步 注册成员信息
        Set<SchedulerServerRegister> clusterMembers = defaultClientHandler.findClusterMembers(defaultClientHandler.findClusterPrefKey());
        clusterMembers.parallelStream()
                .forEach(register -> {
                    RedisMessage realVote = votes.parallelStream()
                            .filter(vote -> Objects.deepEquals(vote.ipMappingPort(),
                                    properties.mergeIpPort(register.getServerIp(), register.getServerPort())))
                            .findFirst()
                            .orElse(null);

                    if (Objects.nonNull(realVote)) {
                        // 发现自己注册信息对应的投票信息, 表示更新成上线
                        register.setRegisterTime(realVote.getRegisterTime())
                                .setDeRegisterTime(null)
                                .setStatus(HostStatusEnum.UP.getType());
                    }

                    String ipMappingPort = register.ipMappingPort();
                    if (!serverAddress.contains(ipMappingPort)) {
                        // 如果要删除的包括这个，那么此条信息要删除掉, vote , clusterMember二个信息
//                        String memberKey = message.getClusterMemberKey();
//                        String voteKey = message.getVoteKey();
//                        defaultClientHandler.removeByKey(memberKey);
//                        defaultClientHandler.removeByKey(voteKey);
                    } else {
                        // 如果不是要直接删除的信息，才有资格进行更新
                        if (destroy) {
                            // 表示自己要消毁
                            register.setDeRegisterTime(now);
                            register.setStatus(HostStatusEnum.DOWN.getType());
                        }
                        defaultClientHandler.updateMessageByKey(message.getClusterMemberKey(), JsonUtils.toJson(register));
                    }
                });

        List<SchedulerServerRegister> clusterListTemp = clusterList;
        clusterMembers.parallelStream().forEach(f -> {
            SchedulerServerRegister register = clusterListTemp.parallelStream().filter(temp -> Objects.deepEquals(f.ipMappingPort(), temp.ipMappingPort()))
                    .findFirst().orElseGet(() -> null);
            if (Objects.isNull(register)) {
                clusterListTemp.add(f);
            }
        });

        String josn = JsonUtils.toJson(clusterList);
        redisUtil.set(message.getClusterListKey(), josn);
    }

    /**
     * 集群列表全部下线
     * 1. 把注册信息 2. 注册列表信息 3. 投票信息 4. 负载均衡信息 5. 下次负载均衡主机信息 全部清除
     *
     * @param currentServer
     * @param leader
     */
    @CacheLock(prefix = "offLineClusterList", expire = 60)
    public synchronized void offLineClusterList(RedisMessage currentServer, RedisMessage leader) {
        // 1. 获取心跳列表记录
        Set<String> keys = defaultClientHandler.findKeys(currentServer.getHeartbeatPrefKey() + "*");
        // 2. 通过心跳列表, 获取所有心跳单条记录数据
        Set<SchedulerServerHeartbeat> heartbeats = keys.parallelStream()
                .map(m -> defaultClientHandler.findByKey(m, SchedulerServerHeartbeat.class))
                .collect(Collectors.toSet());

        // 如果没有心跳记录，要标记所有列表离线
        // 1. 把注册信息 2. 注册列表信息 3. 投票信息 4. 负载均衡信息 5. 下次负载均衡主机信息 全部清除
        Set<String> clusterRegisterKeys = defaultClientHandler.findKeys(currentServer.getClusterPrefKey() + "*");
        Set<SchedulerServerRegister> registers = clusterRegisterKeys.parallelStream()
                .map(m -> defaultClientHandler.findByKey(m, SchedulerServerRegister.class))
                .collect(Collectors.toSet());
        List<SchedulerServerRegister> clusterList = defaultClientHandler.findClusterList();
        Set<RedisMessage> votes = defaultClientHandler.findVotes(currentServer.getVoteKeyPref());

        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.ofHours(8));

        // 所有心跳匹配后，离线主机
        Set<String> offLine = heartbeats.parallelStream().filter(f -> {
            long heartbeatTimeout = properties.getClusterHeartbeatTimeout();
            LocalDateTime lastHeartbeatTime = f.getLastHeartbeatTime();
            long lastSecond = lastHeartbeatTime.toEpochSecond(ZoneOffset.ofHours(8));
            return nowSecond - lastSecond > heartbeatTimeout;
        }).map(SchedulerServerHeartbeat::ipMappingPort)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(offLine)) {
            // 表示除了leader 以外，已经全离线
            votes.parallelStream().forEach(f -> {
                if (!Objects.deepEquals(f.ipMappingPort(), leader.ipMappingPort())) {
                    offLine.add(f.ipMappingPort());
                }
            });
        }

        votes.parallelStream().forEach(vote -> {
            // 找出这个主机，如果离线就把相关数据清洗掉
            String ipMappingPort = vote.ipMappingPort();
            if (offLine.contains(ipMappingPort)) {
                // 1. 把注册信息
                registers.parallelStream().forEach(register -> {
                    if (Objects.deepEquals(ipMappingPort, register.ipMappingPort())) {
                        // 注册信息离线
                        register.setDeRegisterTime(now)
                                .setStatus(HostStatusEnum.DOWN.getType());
                    }
                    defaultClientHandler.updateMessageByKey(vote.getClusterMemberKey(), JsonUtils.toJson(register));
                });
                // 2. 注册列表信息
                clusterList.parallelStream().forEach(member -> {
                    if (Objects.deepEquals(ipMappingPort, member.ipMappingPort())) {
                        // 注册信息离线
                        member.setDeRegisterTime(now)
                                .setStatus(HostStatusEnum.DOWN.getType());
                    }
                });
                // 表示指定此成员离线
                this.memberOffLine(vote);
            }
        });
    }

    /**
     * 集群投票处理
     *
     * @param message
     */
    @CacheLock(prefix = "vote", expire = 60)
    public synchronized void vote(RedisMessage message) {
        Set<String> serverAddress = properties.findServerAddress();

        // 查看所有投票，投票数大于等于集群数量一半的为 leader
        Set<String> keys = redisUtil.keys(message.getVoteKeyPref() + "*");
        // properties list validate real vote list
        if (keys.size() < serverAddress.size()) {
            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------------------------------------------------");
                log.info(">>> 当前投票成员数量不足, 存在未启动集群成员服务器, 无法选举...");
                log.info(">>> ----------------------------------------------------------------------");
            }
            return;
        }

        // 获取所有有效选举投票信息
        List<RedisMessage> realVotes = new ArrayList<>(keys.size());
        keys.parallelStream().forEach(key -> {
            Object o = redisUtil.get(key);
            if (Objects.nonNull(o)) {
                RedisMessage raftMessage = JsonUtils.fromJson(o.toString(), RedisMessage.class);
                assert raftMessage != null;
                RaftStatusEnum currentRaftStatus = raftMessage.getCurrentRaftStatus();
                String vote = raftMessage.getVote();
                long voteTime = raftMessage.getVoteTime();
                long nowSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));

                // vote status validate
                if (currentRaftStatus == RaftStatusEnum.CANDIDATE
                        && Objects.nonNull(vote)
                        && serverAddress.contains(raftMessage.ipMappingPort())
                        && nowSecond >= voteTime
                ) {
                    realVotes.add(raftMessage);
                }
            }
        });

        if (realVotes.size() < serverAddress.size()) {
            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------------------------------------------------");
                log.info(">>> 当前投票成员有效投票数量不足, 存在无效投票, 无法选举...");
                log.info(">>> ----------------------------------------------------------------------");
            }
            return;
        }

        // 计算真实有效投票数量
        Map<String, Integer> voteIpPortMappingCount = new HashMap<>(serverAddress.size());
        if (!CollectionUtils.isEmpty(realVotes)) {
            realVotes.forEach(f -> {
                Integer count = voteIpPortMappingCount.getOrDefault(f.getVote(), 0);
                // vote count
                ++count;
                RaftStatusEnum currentRaftStatus = f.getCurrentRaftStatus();
                if (currentRaftStatus == RaftStatusEnum.CANDIDATE) {
                    voteIpPortMappingCount.put(f.getVote(), count);
                }
            });
        }

        if (voteIpPortMappingCount.size() < serverAddress.size()) {
            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------------------------------------------------");
                log.info(">>> 当前投票成员有效投票数量有效, 统计数量 < 投票数量, 无法选举...");
                log.info(">>> ----------------------------------------------------------------------");
            }
            return;
        }

        // 找出投票数量最多为 leader
        Optional<Map.Entry<String, Integer>> max = voteIpPortMappingCount
                .entrySet()
                .parallelStream()
                .max(Map.Entry.comparingByValue());

        if (max.isPresent()) {
            Map.Entry<String, Integer> entry = max.get();
            // leader ip
            String leaderHostAddress = entry.getKey();

            RedisMessage leaderMessage = realVotes.parallelStream()
                    .filter(f -> Objects.deepEquals(f.getVoteHost() + ":" + f.getRegisterPort(), leaderHostAddress))
                    .findFirst().get();
            leaderMessage.setCurrentRaftStatus(RaftStatusEnum.LEADER);
            if (Objects.deepEquals(leaderMessage.ipMappingPort(), message.ipMappingPort())) {
                message.setCurrentRaftStatus(leaderMessage.getCurrentRaftStatus());
            }

            // leader new bean
            RedisMessage leader = new RedisMessage();
            BeanUtils.copyProperties(leaderMessage, leader);
            leader.setVote(null);
            leader.setEnableHealthCheck(true);

            String leaderJson = JsonUtils.toJson(leader);
            // 缓存新Leader
            redisUtil.set(message.getLeaderKey(), leaderJson);
            // 同步新Leader
            groupServer.setLeader(leader);

            // leader old bean
            String voteJson = JsonUtils.toJson(leaderMessage);
            // 保存旧投票信息
            boolean isLeader = Objects.deepEquals(leader.ipMappingPort(), message.ipMappingPort());
            message.setCurrentRaftStatus(isLeader ? RaftStatusEnum.LEADER : RaftStatusEnum.FOLLOWER);
            redisUtil.set(message.getVoteKey(), voteJson);

            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------------------------------------------------");
                voteIpPortMappingCount.entrySet().parallelStream()
                        .forEach(entrySet -> log.info(">>> {} => {} 票", entrySet.getKey(), entrySet.getValue()));
                log.info(">>> 投票成功, 选举出 Leader: {}", leaderHostAddress);
                log.info(">>> ----------------------------------------------------------------------");
            }

            /*
              ================================================
              ===== 通知leader 接管集群, 通知集群成员开始心跳 =====
              ================================================
             */
            this.clusterFlushStatus(message);
        } else {
            if (properties.isAllowLog()) {
                log.info(">>> ----------------------------------------------------------------------");
                voteIpPortMappingCount.entrySet().parallelStream()
                        .forEach(entry -> log.info(">>> {} => {} 票", entry.getKey(), entry.getValue()));
                log.info(">>> 投票失败, 未选举出 Leader...");
                log.info(">>> ----------------------------------------------------------------------");
            }
        }
    }

    public void clusterFlushStatus(RedisMessage message) {
        // 进入集群成员自我心跳时间
        defaultClientHandler.eventPush(RedisListenerEnum.CLUSTER_MEMBER_HEARTBEAT, JsonUtils.toJson(message));

        // Leader进入刷新管理时间
        defaultClientHandler.eventPush(RedisListenerEnum.CLUSTER_REFRESH, JsonUtils.toJson(message));
    }

    /**
     * 集群 Leader, 为下次负载均衡准备服务端
     *
     * @param currentServer
     * @param leader
     */
    public void clusterLoadBalance(RedisMessage currentServer,
                                   RedisMessage leader
    ) {
        if (Objects.deepEquals(currentServer.ipMappingPort(), leader.ipMappingPort())) {
            Object next = redisUtil.get(currentServer.getBalanceNextKey());

            // 如果负载均衡主机，已下线，放弃负载
            if (Objects.nonNull(next)) {
                String temp = next.toString();
                List<SchedulerServerRegister> clusterList = defaultClientHandler.findClusterList();
                SchedulerServerRegister register = clusterList
                        .parallelStream()
                        .filter(f -> Objects.deepEquals(HostStatusEnum.UP.getType(), f.getStatus()))
                        .filter(f -> Objects.deepEquals(f.ipMappingPort(), temp)).findFirst()
                        .orElse(null);
                if (Objects.isNull(register)) {
                    next = null;
                }
            }

            if (Objects.nonNull(next)) {
                if (properties.isAllowLog()) {
                    log.info(">>> 集群负载均衡加载成功, 客户端下次连接集群成员: {}", next);
                }
            } else {
                defaultClientHandler.loadNextBalance(currentServer.getBalanceNextKey());
            }
        }
    }

    /**
     * 消毁指定 key
     *
     * @param key
     */
    public synchronized void destroy(String key) {
        redisUtil.delete(key);
    }

    /**
     * 消毁指定集群成员 所有信息
     *
     * @param message 集群成员主机信息
     */
    public synchronized void destroyLeaderAndMember(RedisMessage message) {
        String leaderMessage = defaultClientHandler.findLeaderMessage();
        RedisMessage leader = JsonUtils.fromJson(leaderMessage, RedisMessage.class);
        assert leader != null;

        try {
            // 消毁集群Leader信息
            if (Objects.deepEquals(leader.ipMappingPort(), message.ipMappingPort())) {
                this.destroy(message.getLeaderKey());
            }
        } catch (Exception e) {
            log.error(">>> 集群Leader退出, 消毁Leader信息失败, {}", e.getLocalizedMessage(), e);
        }

        try {
            // 消毁集群成员自己信息
            SchedulerServerRegister register = defaultClientHandler.findByKey(message.getClusterMemberKey(), SchedulerServerRegister.class);
            log.info(">>>>> 消毁集群成员自己信息: {}", register);
            register.setDeRegisterTime(LocalDateTime.now())
                    .setStatus(HostStatusEnum.DOWN.getType());
            defaultClientHandler.updateMessageByKey(message.getClusterMemberKey(), JsonUtils.toJson(register));
        } catch (Exception e) {
            log.error(">>> 集群成员退出, 消毁注册信息失败, {}", e.getLocalizedMessage(), e);
        }

        try {
            // 消毁集群成员自己投票信息
            this.destroy(message.getVoteKey());
        } catch (Exception e) {
            log.error(">>> 集群成员退出, 消毁投票信息失败, {}", e.getLocalizedMessage(), e);
        }

        try {
            // 消毁集群成员自己负载均衡信息
            this.destroy(message.getBalanceKey());
        } catch (Exception e) {
            log.error(">>> 集群成员退出, 消毁自己负载均衡信息失败, {}", e.getLocalizedMessage(), e);
        }

        try {
            this.destroy(message.getBalanceNextKey());
        } catch (Exception e) {
            log.error(">>> 集群成员退出,删除负载均衡信息失败, {}", e.getLocalizedMessage(), e);
        }

        String heartbeatKey = message.getHeartbeatKey();
        this.destroy(heartbeatKey);

        try {
            // 重新初始化集群列表，标记自己下线
            this.flushClusterList(message, true);
        } catch (Exception e) {
            log.error(">>> 集群成员退出, 刷新集群列表信息失败, {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 表达式任务扫描，然后指派指定任务的执行器客户端
     *
     * @param tasks
     */
    @CacheLock(prefix = "scanningCronSchedulerTasks", expire = 60)
    @Transactional(rollbackFor = Exception.class)
    public void scanningCronSchedulerTasks(Set<SchedulerTaskCronRequestDto> tasks) {
        Set<SchedulerTaskCronRequestDto> cronTasks = tasks.parallelStream()
                .filter(f -> Objects.deepEquals(f.getTaskCronStatus(), Constants.RUNNING))
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(cronTasks)) {
            this.cronTasksHandler(cronTasks);
        }
    }

    private void cronTasksHandler(Set<SchedulerTaskCronRequestDto> cronTasks) {
        if (CollectionUtils.isEmpty(cronTasks)) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        log.info(">>>>>>> cronTasksHandler: {}", cronTasks.size());
        cronTasks.parallelStream().forEach(f -> {
            try {
                CronExpression cronExpression = new CronExpression(f.getTaskCronExpression());
                boolean satisfiedBy = cronExpression.isSatisfiedBy(now);
                log.info(">>>>>>>>>>>>> {} : {}", now, satisfiedBy);
                // 表达式执行时间已到，需要找出执行器并执行
                if (satisfiedBy) {
                    // 找出一个执行器开始执行
                    String appName = f.getTaskCronAppName();

                    SchedulerTaskMessage taskMessage = new SchedulerTaskMessage();
                    taskMessage.setCron(true)
                            .setActuatorKey(appName)
                            .setAppName(appName)
                            .setJobHandler(f.getTaskCronJobHandler())
                            .setSchedulerCron(f.getTaskCronExpression())
                            .setJobHandlerParam(f.getTaskCronParam())
                            .setTaskCronDto(f)
                    ;
                    String json = JsonUtils.toJson(taskMessage);
                    defaultClientHandler.eventPush(RedisListenerEnum.SCHEDULER_NOTIFY, json);
                }
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * 指定时间任务扫描，然后指派指定任务的执行器客户端
     *
     * @param tasks
     * @param channel
     */
    @CacheLock(prefix = "scanningGivenSchedulerTasks", expire = 60)
    @Transactional(rollbackFor = Exception.class)
    public void scanningGivenSchedulerTasks(Set<SchedulerTaskGivenRequestDto> tasks, Channel channel) {
        Set<SchedulerTaskGivenRequestDto> givenTasks = tasks.parallelStream()
                .filter(f -> Objects.deepEquals(f.getTaskGivenStatus(), Constants.RUNNING))
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(givenTasks)) {
            this.givenTasksHandler(givenTasks, channel);
        }
    }

    /**
     * 0: 未执行 1: 执行中 2: 待重试
     * <p>
     * 1. 扫描未执行的，检验是否可执行
     * 2. 扫描执行中的，跳过，不处理
     * 3. 扫描待重试的，检验上次执行时间 + 间隔时间 >= 当前时间， 是： 累计重试次数，重新执行，修改状态为执行中, 如果执行上限，直接回写
     *
     * @param givenTasks
     * @param channel
     */
    private void givenTasksHandler(Set<SchedulerTaskGivenRequestDto> givenTasks, Channel channel) {
        log.info(">>>>>>> givenTasksHandler: {}", givenTasks.size());

        if (CollectionUtils.isEmpty(givenTasks)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        long newSecond = now.toEpochSecond(ZoneOffset.ofHours(8));

        {
            // 1. 扫描未执行的，检验是否可执行
            Set<SchedulerTaskGivenRequestDto> waitExecuteList = givenTasks.parallelStream()
                    .filter(f -> Objects.deepEquals(f.getTaskGivenExecuteStatus(), Constants.TASK_WAIT_EXECUTE))
                    .collect(Collectors.toSet());

            waitExecuteList.parallelStream().forEach(f -> {
                LocalDateTime schedulerGivenTime = f.getTaskGivenTime();
                long second = schedulerGivenTime.toEpochSecond(ZoneOffset.ofHours(8));
                if (StringUtils.isNotBlank(f.getTaskGivenDelayed())) {
                    long schedulerGivenDelay = Long.parseLong(f.getTaskGivenDelayed());
                    second += schedulerGivenDelay;
                }

                if (newSecond >= second) {
                    String appName = f.getTaskGivenAppName();

                    SchedulerTaskMessage taskMessage = new SchedulerTaskMessage();
                    taskMessage.setCron(false)
                            .setActuatorKey(appName)
                            .setAppName(appName)
                            .setJobHandler(f.getTaskGivenJobHandler())
                            .setSchedulerGivenTime(f.getTaskGivenTime())
                            .setJobHandlerParam(f.getTaskGivenParam())
                            .setTaskGivenDto(f)
                    ;
                    // 修改状态为执行中
                    SchedulerTaskGivenRequestDto updateDto = new SchedulerTaskGivenRequestDto();
                    updateDto.setTaskGivenId(f.getTaskGivenId());
                    updateDto.setTaskGivenExecuteStatus(Constants.TASK_EXECUTING);

                    boolean update = taskGivenService.updateGivenExecuteStatus(updateDto);
                    if (update) {
                        String json = JsonUtils.toJson(taskMessage);
                        defaultClientHandler.eventPush(RedisListenerEnum.SCHEDULER_NOTIFY, json);
                    }
                }
            });
        }

        {
            // 3. 扫描待重试的，检验上次执行时间 + 间隔时间 >= 当前时间， 是： 累计重试次数，重新执行，修改状态为执行中
            Set<SchedulerTaskGivenRequestDto> waitExecuteList = givenTasks.parallelStream()
                    .filter(f -> Objects.deepEquals(f.getTaskGivenExecuteStatus(), Constants.TASK_WAIT_RETRY))
                    .collect(Collectors.toSet());

            waitExecuteList.parallelStream().forEach(f -> {

                // 上次执行时间
                LocalDateTime lastExecuteTime = f.getTaskGivenLastExecuteTime();
                long lastExecuteSecond = lastExecuteTime.toEpochSecond(ZoneOffset.ofHours(8));
                if (Objects.nonNull(f.getTaskGivenRetryDelayed())) {
                    lastExecuteSecond += f.getTaskGivenRetryDelayed();
                }

                // 检验上次执行时间 + 间隔时间 >= 当前时间
                if (newSecond >= lastExecuteSecond) {
                    String appName = f.getTaskGivenAppName();

                    SchedulerTaskMessage taskMessage = new SchedulerTaskMessage();
                    taskMessage.setCron(false)
                            .setActuatorKey(appName)
                            .setAppName(appName)
                            .setJobHandler(f.getTaskGivenJobHandler())
                            .setSchedulerGivenTime(f.getTaskGivenTime())
                            .setTaskGivenDto(f)
                            .setJobHandlerParam(f.getTaskGivenParam())
                    ;
                    Integer retryCount = f.getTaskGivenRetryCount();

                    boolean allowExecute = this.allowExecute(f, now, channel);
                    if (allowExecute) {
                        // 修改状态为执行中
                        SchedulerTaskGivenRequestDto updateDto = new SchedulerTaskGivenRequestDto();
                        updateDto.setTaskGivenId(f.getTaskGivenId());
                        updateDto.setTaskGivenExecuteStatus(Constants.TASK_EXECUTING);
                        updateDto.setTaskGivenLastExecuteTime(now);
                        updateDto.setTaskGivenRetryCount(Objects.nonNull(retryCount) ? retryCount + 1 : 1);

                        boolean update = taskGivenService.updateGivenExecuteStatus(updateDto);
                        if (update) {
                            String json = JsonUtils.toJson(taskMessage);
                            defaultClientHandler.eventPush(RedisListenerEnum.SCHEDULER_NOTIFY, json);
                        }
                    }
                }
            });
        }
    }

    /**
     * 此处必须不加事务，调用此方法的地方必须加，此方法为联合调用子方法
     *
     * @param query
     * @param now
     * @param channel
     * @return
     */
    private boolean allowExecute(SchedulerTaskGivenRequestDto query, LocalDateTime now, Channel channel) {
        Integer taskGivenRetryCount = query.getTaskGivenRetryCount();
        Integer taskGivenRetryMax = query.getTaskGivenRetryMax();

        if (Objects.nonNull(taskGivenRetryCount) && Objects.nonNull(taskGivenRetryMax)) {
            if (taskGivenRetryMax > 0 && taskGivenRetryCount >= taskGivenRetryMax) {
                // 如果当前已重试次数 > 重试最大值 ，不允许调度
                // 修改状态为执行中
                SchedulerTaskGivenRequestDto updateDto = new SchedulerTaskGivenRequestDto();
                updateDto.setTaskGivenId(query.getTaskGivenId());
                updateDto.setTaskGivenExecuteStatus(Constants.TASK_ALREADY_EXECUTE);

                boolean update = taskGivenService.updateGivenExecuteStatus(updateDto);
                if (!update) {
                    return true;
                }

                String taskGivenWriteLog = query.getTaskGivenWriteLog();
                if (Objects.deepEquals(taskGivenWriteLog, Constants.RUNNING)) {
                    SchedulerRegistryDetail registryDetail = registryDetailService.getBaseMapper().selectById(query.getTaskGivenRegistryDetailId());
                    if (Objects.nonNull(registryDetail)) {
                        SchedulerTaskLogRequestDto taskLogRequest = new SchedulerTaskLogRequestDto();
                        taskLogRequest.setLogRegistryDetailId(query.getTaskGivenRegistryDetailId())
                                .setLogTaskId(query.getTaskGivenId())
                                .setLogExecutorAddress(registryDetail.getRegisterDetailIp() + ":" + registryDetail.getRegisterDetailPort())
                                .setLogExecutorHandler(query.getTaskGivenJobHandler())
                                .setLogExecutorParam(query.getTaskGivenParam())
                                .setLogTriggerTime(now)
                                .setLogTriggerCode(String.valueOf(HttpStatus.OK.value()))
                                .setLogTriggerMsg(null)
                                .setLogHandleTime(now)
                                .setLogHandleCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                .setLogHandleMsg("重试次数已达上限")
                                .setLogTaskType(Constants.TASK_TYPE_OF_GIVEN)
                                .setLogCreateTime(LocalDateTime.now())
                        ;

                        SchedulerTaskLog logEntity = new SchedulerTaskLog();
                        BeanUtils.copyProperties(taskLogRequest, logEntity);
                        boolean save = taskLogService.save(logEntity);
                        if (save) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
