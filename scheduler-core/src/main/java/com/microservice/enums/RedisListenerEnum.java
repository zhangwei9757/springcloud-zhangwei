package com.microservice.enums;

/**
 * @author zw
 * @date 2020-10-02
 * <p>
 */
public enum RedisListenerEnum {

    /**
     * 集群心跳监管处理通知
     */
    CLUSTER_HEARTBEAT_MANAGER("scheduler_cluster_heartbeat_manager"),

    /**
     * 集群成员自我心跳通知
     */
    CLUSTER_MEMBER_HEARTBEAT("scheduler_cluster_member_heartbeat"),

    /**
     * 集群刷新管理通知
     */
    CLUSTER_REFRESH("scheduler_cluster_refresh"),

    /**
     * 开始选举通知
     */
    RAFT("scheduler_raft"),

    /**
     * 集群成员通知
     */
    SCHEDULER_NOTIFY("scheduler_notify"),

    /**
     * 同步客户端消息通知
     */
    SYNC_CLIENT_MESSAGE("scheduler_sync_client_message");

    private String type;

    RedisListenerEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
