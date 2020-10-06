package com.microservice.utils;

/**
 * @author zw
 * @date 2020-10-01
 * <p>
 */
public class Constants {
    public static final int REDIS_CACHE_INVALID_TIME = 1000 * 60 * 60 * 24;

    public static final String DUPLICATE_LOCK = "duplicateLock:";
    public static final String SCHEDULER_PREF = "scheduler:";
    public static final String SCHEDULER_SERVER_CLUSTER_LIST = SCHEDULER_PREF + "clusterList";
    public static final String SCHEDULER_SERVER_CLUSTER_PREF = SCHEDULER_PREF + "cluster:";
    public static final String SCHEDULER_SERVER_VOTE_PREF = SCHEDULER_PREF + "vote:";
    public static final String SCHEDULER_SERVER_LEADER = SCHEDULER_PREF + "leader";
    public static final String SCHEDULER_SERVER_BALANCE_PREF = SCHEDULER_PREF + "balance:";
    public static final String SCHEDULER_SERVER_NEXT_BALANCE = SCHEDULER_PREF + "nextBalance";
    public static final String SCHEDULER_SERVER_HEARTBEAT = SCHEDULER_PREF + "heartbeat:";
}
