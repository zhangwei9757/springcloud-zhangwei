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
 * <p>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerServerRegister implements Serializable {

    private static final long serialVersionUID = 1323807153767767353L;
    private String serverIp;
    private int serverPort;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deRegisterTime;
    private String status;

    @JsonIgnore
    public String ipMappingPort() {
        return this.serverIp + ":" + this.serverPort;
    }
}
