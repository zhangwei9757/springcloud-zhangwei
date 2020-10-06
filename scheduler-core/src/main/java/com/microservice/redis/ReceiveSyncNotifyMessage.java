package com.microservice.redis;

import com.microservice.bean.SyncNotifyMessage;

/**
 * @author zw
 * @date 2020-10-06
 * <p> 当前集群成员，接收到消息处理器，默认不处理，如果服务器需要处理，自定义实现
 */
public interface ReceiveSyncNotifyMessage {

    /**
     * 集群同步客户端接收消息处理器
     *
     * @param notifyMessage
     */
    void onMessage(SyncNotifyMessage notifyMessage);
}
