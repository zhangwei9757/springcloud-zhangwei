package com.microservice.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zw
 * @date 2020-10-01
 * <p> 集群心跳
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerServerHeartbeat implements Serializable {

    private static final long serialVersionUID = -4111449591354428084L;
    private String serverIp;
    private int serverPort;
    private String leaderHostAddress;
    private int leaderHostPort;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastHeartbeatTime;

    @JsonIgnore
    public String ipMappingPort() {
        return this.serverIp + ":" + this.serverPort;
    }
}
