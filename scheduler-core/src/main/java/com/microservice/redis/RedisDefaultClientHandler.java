package com.microservice.redis;

import com.google.common.collect.Sets;
import com.microservice.annotation.lock.CacheLock;
import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SchedulerServerRegister;
import com.microservice.enums.HostStatusEnum;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.utils.Constants;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.RandomUtils;
import com.microservice.utils.RedisUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zw
 * @date 2020-10-02
 * <p>
 */
@Service
@Slf4j
public class RedisDefaultClientHandler {

    @Resource
    private StringRedisTemplate template;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SchedulerConfigurationProperties properties;

    /**
     * 服务器启动校验
     */
    public void validateProperties(boolean validate) {
        Set<String> serverAddress = properties.findServerAddress();
        if (CollectionUtils.isEmpty(serverAddress)) {
            throw new RuntimeException("microservice.scheduler.serverAddress empty is not allowed...");
        }

        long count = serverAddress.parallelStream().filter(f -> f.split(":").length <= 1).count();
        if (count > 0) {
            throw new RuntimeException("microservice.scheduler.serverAddress The format is ip:port;ip:port...");
        }

        String[] notAllow = {"127.0.0.1", "localhost"};
        long errHostCounts = serverAddress.parallelStream()
                .filter(f -> StringUtils.containsAny(f, notAllow))
                .count();
        if (errHostCounts > 0) {
            throw new RuntimeException("microservice.scheduler.serverAddress Local is not allowed...");
        }

        if (validate) {
            String hostAddress = this.getHostAddress();
            boolean allow = false;
            for (String address : serverAddress) {
                if (address.contains(hostAddress)) {
                    String[] split = address.split(":");
                    if (split.length > 1 && split[1].equals(String.valueOf(properties.getServerPort()))) {
                        allow = true;
                    }
                }
            }

            if (!allow) {
                throw new RuntimeException("ip or port is not allowed...");
            }
        }

        String registerName = properties.getRegisterName();
        String clusterName = properties.getClusterName();
        if (StringUtils.isAnyBlank(registerName, clusterName)) {
            throw new RuntimeException("microservice.scheduler.clusterName|registerName Empty is not allowed...");
        }
    }

    /**
     * 更新指定key 对应的值 [选举]
     *
     * @param key
     * @param value
     */
    public void updateMessageByKey(String key, String value) {
        redisUtil.set(key, value);
    }

    /**
     * 集群事件推送
     *
     * @param redisListener 订阅类型
     * @param message       推送消息
     */
    public void eventPush(RedisListenerEnum redisListener, String message) {
        String listenerType = redisListener.getType();
        template.convertAndSend(listenerType, message);
    }

    /**
     * 上线，离线 [false: 标记下线, true: 标记上线]
     *
     * @param message
     * @param onLine
     */
    public void onLineOrOffLine(RedisMessage message, boolean onLine) {
        SchedulerServerRegister register = this.findByKey(message.getClusterMemberKey(), SchedulerServerRegister.class);
        if (Objects.nonNull(register)) {
            if (onLine) {
                register.setDeRegisterTime(null);
                register.setStatus(HostStatusEnum.UP.getType());
            } else {
                register.setDeRegisterTime(LocalDateTime.now());
                register.setStatus(HostStatusEnum.DOWN.getType());
            }
            redisUtil.set(message.getClusterMemberKey(), JsonUtils.toJson(register));
        }

        List<SchedulerServerRegister> clusterList = this.findClusterList();
        if (!CollectionUtils.isEmpty(clusterList)) {
            clusterList.parallelStream().forEach(f -> {
                if (Objects.deepEquals(f.ipMappingPort(), message.ipMappingPort())) {
                    if (onLine) {
                        f.setDeRegisterTime(null);
                        f.setStatus(HostStatusEnum.UP.getType());
                    } else {
                        f.setDeRegisterTime(LocalDateTime.now());
                        f.setStatus(HostStatusEnum.DOWN.getType());
                    }
                }
            });
            redisUtil.set(message.getClusterListKey(), JsonUtils.toJson(clusterList));
        }

        if (!onLine) {
            String heartbeatKey = message.getHeartbeatKey();
            redisUtil.delete(heartbeatKey);
        }
    }

    /**
     * 查询集群列表, 并显示是否在线
     *
     * @return
     */
    public List<SchedulerServerRegister> findClusterList() {
        String clusterListKey = this.findClusterListKey();
        Object o = redisUtil.get(clusterListKey);

        List<SchedulerServerRegister> cluster = new ArrayList<>(0);
        if (Objects.isNull(o)) {
            return cluster;
        }

        List<SchedulerServerRegister> tempCluster = (List<SchedulerServerRegister>) JsonUtils
                .transformCollectionsfromJson(o.toString(), SchedulerServerRegister.class, List.class);

        if (CollectionUtils.isEmpty(tempCluster)) {
            return cluster;
        }
        return tempCluster;
    }

    /**
     * 查询集群Leader  [1]
     *
     * @return
     */
    public String findLeaderMessage() {
        Object o = redisUtil.get(this.findLeaderKey());
        return Objects.isNull(o) ? null : o.toString();
    }

    /**
     * 查询集群Leader  [1]
     *
     * @return
     */
    public RedisMessage findLeader() {
        String leaderMessage = this.findLeaderMessage();
        if (StringUtils.isNotBlank(leaderMessage)) {
            return JsonUtils.fromJson(leaderMessage, RedisMessage.class);
        }
        return null;
    }

    /**
     * 查询指定服务器信息
     *
     * @param key
     * @return
     */
    public <T> T findByKey(String key, Class<T> clazz) {
        Object o = redisUtil.get(key);
        return Objects.isNull(o) ? null : JsonUtils.fromJson(o.toString(), clazz);
    }

    /**
     * 查询指定类型列表
     *
     * @param keys
     * @return
     */
    public Set<String> findKeys(String keys) {
        return redisUtil.keys(keys);
    }


    /**
     * 删除指定缓存数据
     *
     * @param key
     */
    public void removeByKey(String key) {
        redisUtil.delete(key);
    }

    /**
     * 获取本机ip地址
     *
     * @return
     */
    public String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(">>> 获取本机ip地址失败, 原因: {}", e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 查询集群成员注册列表
     *
     * @param clusterPref
     * @return
     */
    public Set<SchedulerServerRegister> findClusterMembers(String clusterPref) {
        Set<String> keys = redisUtil.keys(clusterPref + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return new HashSet<>(0);
        }

        Set<SchedulerServerRegister> rtns = new HashSet<>(keys.size());
        keys.parallelStream().forEach(f -> {
            Object o = redisUtil.get(f);
            if (Objects.nonNull(o)) {
                SchedulerServerRegister register = JsonUtils.fromJson(o.toString(), SchedulerServerRegister.class);
                rtns.add(register);
            }
        });
        return rtns;
    }

    /**
     * 注册列表，转换成集群列表
     *
     * @param serverAddress
     * @param hostAddress
     * @param serverPort
     * @return
     */
    @CacheLock(prefix = "serverAddress2ClusterMembers", expire = 60)
    public Set<SchedulerServerRegister> serverAddress2ClusterMembers(Set<String> serverAddress, String hostAddress, int serverPort) {
        Set<SchedulerServerRegister> rtns = new HashSet<>(0);
        if (CollectionUtils.isEmpty(serverAddress)) {
            return rtns;
        }

        rtns = serverAddress.parallelStream()
                .map(m -> new SchedulerServerRegister(
                                m.split(":")[0],
                                Integer.parseInt(m.split(":")[1]),
                                m.equals(hostAddress + ":" + serverPort) ? LocalDateTime.now() : null,
                                null,
                                m.equals(hostAddress + ":" + serverPort) ?
                                        HostStatusEnum.UP.getType() :
                                        HostStatusEnum.UNKNOWN.getType()
                        )
                )
                .collect(Collectors.toSet());
        return rtns;
    }

    /**
     * 是否存在集群成员注册信息
     *
     * @param clusterMemberKey
     * @return
     */
    public boolean hasClusterMemberRegister(String clusterMemberKey) {
        return redisUtil.hasKey(clusterMemberKey);
    }

    /**
     * 查询集群成员投票信息
     *
     * @return
     */
    public Set<RedisMessage> findVotes(String votePref) {
        Set<String> keys = redisUtil.keys(votePref + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return new HashSet<>(0);
        }

        Set<RedisMessage> rtns = new HashSet<>(keys.size());
        keys.parallelStream().forEach(f -> {
            Object o = redisUtil.get(f);
            if (Objects.nonNull(o)) {
                RedisMessage redisRaftMessage = JsonUtils.fromJson(o.toString(), RedisMessage.class);
                rtns.add(redisRaftMessage);
            }
        });
        return rtns;
    }

    /**
     * 是否存在集群成员投票信息
     *
     * @param votePref
     * @return
     */
    public boolean hasVotes(String votePref) {
        Set<String> keys = redisUtil.keys(votePref + "*");
        return !CollectionUtils.isEmpty(keys);
    }

    /**
     * 集群负载均衡主机信息
     *
     * @return
     */
    public synchronized String nextBalance(String nextBalanceKey) {
        Object o = redisUtil.get(nextBalanceKey);
        String next = Objects.nonNull(o) ? o.toString() : null;
        // 每次拿了一个后，下次再继续保存
        loadNextBalance(nextBalanceKey);
        return next;
    }

    public synchronized void loadNextBalance(String nextBalanceKey) {
        // 1. 如果在线主机为空，直接返回
        Set<RedisMessage> votes = findVotes(this.findVotePrefKey());

        if (!CollectionUtils.isEmpty(votes)) {
            // 2. 获取负载均衡数据
            Set<String> balanceHost = this.findKeys(this.findNextBalancePrefKey() + "*");

            // 3. 如果负载均衡数据为空，直接生成
            if (CollectionUtils.isEmpty(balanceHost)) {
                RedisMessage vote = RandomUtils.getInList(votes);
                redisUtil.set(vote.getBalanceKey(), "0");
            } else {
                // 4. 如果负载均衡数据不为空，找出下次负载均衡服务器
                Map<String, Integer> mappingBalanceCount = new HashMap<>(votes.size());

                Map<String, RedisMessage> mappingBalanceRegisterName = new HashMap<>(votes.size());
                votes.parallelStream().forEach(vote -> {
                    Object o = redisUtil.get(vote.getBalanceKey());
                    if (Objects.nonNull(o)) {
                        int realCount = Integer.parseInt(o.toString());
                        Integer balance = mappingBalanceCount.getOrDefault(vote.ipMappingPort(), 0);
                        ++balance;
                        balance += realCount;
                        mappingBalanceCount.put(vote.ipMappingPort(), balance);
                        mappingBalanceRegisterName.put(vote.ipMappingPort(), vote);
                    }
                });

                // 如果没有leader 负载数据，直接向里面添加leader
                RedisMessage leader = this.findLeader();
                if (Objects.nonNull(leader)) {
                    Integer count = mappingBalanceCount.getOrDefault(leader.ipMappingPort(), 0);
                    mappingBalanceCount.put(leader.ipMappingPort(), count);
                    mappingBalanceRegisterName.put(leader.ipMappingPort(), leader);
                }

                // 5. 找出当前次数最小的，生成下次负载记录
                Optional<Map.Entry<String, Integer>> min = mappingBalanceCount
                        .entrySet()
                        .parallelStream()
                        .min(Map.Entry.comparingByValue());
                if (min.isPresent()) {
                    Map.Entry<String, Integer> entry = min.get();
                    String nextBalanceIpPort = entry.getKey();
                    RedisMessage message = mappingBalanceRegisterName.get(nextBalanceIpPort);
                    // 刷新负载均衡数据
                    redisUtil.set(nextBalanceKey, nextBalanceIpPort);
                    // 刷新原负载均衡数量
                    redisUtil.set(message.getBalanceKey(), entry.getValue().toString());
                }
            }
        }
    }

    public String findLeaderKey() {
        return Constants.SCHEDULER_SERVER_LEADER;
    }

    public String findVotePrefKey() {
        return Constants.SCHEDULER_SERVER_VOTE_PREF;
    }

    public String findVoteKey(String suffix) {
        return findVotePrefKey() + suffix;
    }

    public String findNextBalancePrefKey() {
        return Constants.SCHEDULER_SERVER_BALANCE_PREF;
    }

    public String findBalanceKey(String suffix) {
        return findNextBalancePrefKey() + suffix;
    }

    public String findNextBalanceKey() {
        return Constants.SCHEDULER_SERVER_NEXT_BALANCE;
    }

    public String findClusterListKey() {
        return Constants.SCHEDULER_SERVER_CLUSTER_LIST;
    }

    public String findClusterPrefKey() {
        return Constants.SCHEDULER_SERVER_CLUSTER_PREF;
    }

    public String findClusterMemberKey(String suffix) {
        return findClusterPrefKey() + suffix;
    }

    public String findClusterHeartbeatPrefKey() {
        return Constants.SCHEDULER_SERVER_HEARTBEAT;
    }

    public String findClusterHeartbeatKey(String suffix) {
        return Constants.SCHEDULER_SERVER_HEARTBEAT + suffix;
    }

    /**
     * 获取客户端通道自己的ipv4 地址
     *
     * @param channel
     * @return
     */
    public static String findAddressByChannel(Channel channel) {
        SocketAddress socketAddress = channel.localAddress();
        String address = socketAddress.toString();
        address = address.replaceAll("/", "");
        try {
            return address.split(":")[0];
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取所有的服务器url
     *
     * @return
     */
    public Set<String> findAllWebApi() {
        Set<RedisMessage> votes = this.findVotes(Constants.SCHEDULER_SERVER_VOTE_PREF);
        Set<String> urls = votes.parallelStream().map(RedisMessage::getHealthIpPort).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(urls)) {
            return Sets.newHashSet();
        }
        return urls;
    }
}
