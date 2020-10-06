package com.microservice.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zw
 * @date 2020-10-06
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SyncNotifyMessage implements Serializable {

    private static final long serialVersionUID = 3927735285151637738L;

    /**
     * 服务器与客户之前的执行任务消息
     */
    private ExecutorJobMessage jobMessage;

    /**
     * 普通字符串消息
     */
    private String commonlyMessage;

    private String channelKey;

    private String clusterMemberVoteKey;

    private int clusterMemberHashCode;
}
