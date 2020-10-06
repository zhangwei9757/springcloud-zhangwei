package com.microservice.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2020-29-14
 * <p>
 * 调度注册参数
 */
@ConfigurationProperties(prefix = "microservice.scheduler")
@Data
public class SchedulerConfigurationProperties implements Serializable {

    private static final long serialVersionUID = 7694461585821554032L;
    /**
     * Client registration name, cluster member registration name
     */
    private String registerName;
    /**
     * Server boot port
     */
    private int serverPort = 9999;
    private int serverThreads = 128;

    /**
     * Cluster registered address, ip:port;ip:port...
     */
    private String clusterAddress = "";
    /**
     * Unit second
     */
    private long clusterHeartbeat = 5;
    /**
     * Unit second
     */
    private long clusterHeartbeatTimeout = clusterHeartbeat * 2;
    /**
     * Unit second
     */
    private long clusterVoteTime = 5;
    /**
     * Unit second
     */
    private long clusterVoteTimeout = clusterVoteTime * 2;
    private String clusterName = "scheduler";
    private String clusterHealthCheckUrl = "/actuator/health";

    /**
     * Client read timeout, Unit second
     */
    private final long readerIdleTimeNanos = 5;
    /**
     * Client write timeout, Unit second
     */
    private final long writerIdleTimeNanos = 5;
    /**
     * Client read write timeout, Unit second
     */
    private final long allIdleTimeNanos = 10;
    /**
     * ping time interval, Unit second
     */
    private final long ping = writerIdleTimeNanos - 1;

    private final String swaggerBasePath = "";
    private String swaggerTitle = "Scheduler Api Document";
    private String swaggerDescription = "scheduler description";
    private String swaggerTermsOfServiceUrl = "www.schedulerExample.com";
    private final String swaggerName = "scheduler team";
    private final String swaggerUrl = "www.schedulerTeam.com";
    private final String swaggerEmail = "schedulerTeamEmail@example.com";
    private String swaggerLicense = "schedulerLicense";
    private String swaggerLicenseUrl = "www.schedulerLicenseUrl.com";
    private String swaggerVersion = "1.0";

    private boolean allowLog = true;

    /**
     * 默认返回第一个集群主机ip
     *
     * @param index
     * @return
     */
    @JsonIgnore
    public String findServerAddress(int index) {
        if (Objects.isNull(clusterAddress)) {
            throw new RuntimeException("microservice.scheduler.serverAddress value is null or empty...");
        }

        String[] split = clusterAddress.split(";");
        if (index <= 0) {
            return split[0];
        }

        return split[index];
    }

    @JsonIgnore
    public Set<String> findServerAddress() {
        if (Objects.isNull(clusterAddress)) {
            throw new RuntimeException("microservice.scheduler.serverAddress value is null or empty...");
        }

        return Arrays.stream(clusterAddress.split(";")).collect(Collectors.toSet());
    }

    @JsonIgnore
    public String[] findIpPort(String ipPort) {
        if (StringUtils.isNotBlank(ipPort)) {
            return ipPort.split(":");
        }

        return findServerAddress(0).split(":");
    }

    @JsonIgnore
    public String mergeIpPort(String ip, int port) {
        if (!StringUtils.isAllBlank(ip, String.valueOf(port))) {
            return ip + ":" + port;
        }
        return null;
    }
}
