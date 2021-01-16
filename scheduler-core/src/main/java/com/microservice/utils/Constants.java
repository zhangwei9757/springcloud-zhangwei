package com.microservice.utils;

import org.apache.commons.lang3.StringUtils;

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

    /**
     * 0：离线 1：在线
     */
    public static final String OFF_LINE = "0";
    public static final String ON_LINE = "1";

    /**
     * 0：STOP 1：RUNNING
     */
    public static final String STOP = "0";
    public static final String RUNNING = "1";

    /**
     * 0：CRON任务 1：GIVEN任务
     */
    public static final String TASK_TYPE_OF_CRON = "0";
    public static final String TASK_TYPE_OF_GIVEN = "1";

    /**
     * 0: 未执行 1: 执行中 2: 待重试 3: 已执行
     */
    public static final String TASK_WAIT_EXECUTE = "0";
    public static final String TASK_EXECUTING = "1";
    public static final String TASK_WAIT_RETRY = "2";
    public static final String TASK_ALREADY_EXECUTE = "3";

    public static final String ACTUATOR_HEALTH_CHECK_URL = "/healthStatusCheck";
    public static final String ACTUATOR_HEALTH_CHECK_FULL_URL = "/actuator/healthStatusCheck";

    /**
     * 合并 ip port => ip:port
     *
     * @param ip
     * @param port
     * @return
     */
    public static String mergeIpPort(String ip, int port) {
        if (!StringUtils.isAllBlank(ip, String.valueOf(port))) {
            return ip.trim() + ":" + port;
        }
        return null;
    }
}
