package com.microservice.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.enums.RaftStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zw
 * @date 2020-10-02
 * <p>
 */
@Accessors(chain = true)
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessage implements Serializable {

    private static final long serialVersionUID = -2394106663551818011L;

    /**
     * netty 消息标示
     */
    private String channelKey;

    /**
     * raft相关
     */
    private String vote;
    private String voteHost;
    private long voteTime;
    private RaftStatusEnum currentRaftStatus;

    /**
     * 集群相关
     */
    private String clusterName;
    private String registerName;
    private int registerPort;

    /**
     * redis key 相关
     */
    private String clusterPrefKey;
    private String clusterMemberKey;
    private String clusterListKey;
    private String voteKeyPref;
    private String voteKey;
    private String balancePrefKey;
    private String balanceKey;
    private String balanceNextKey;
    private String leaderKey;
    private String heartbeatPrefKey;
    private String heartbeatKey;
    private LocalDateTime registerTime;

    private String healthIpPort;
    private String healthUrl;
    private boolean enableHealthCheck;

    /**
     * 心跳相关
     */
    private SchedulerServerHeartbeat heartbeat;

    @JsonIgnore
    public String ipMappingPort() {
        return this.voteHost + ":" + this.registerPort;
    }

    @JsonIgnore
    public String mergeHealthUrl(String healthCheckUrl) {
        return "http://" + this.healthIpPort + healthCheckUrl;
    }
}
