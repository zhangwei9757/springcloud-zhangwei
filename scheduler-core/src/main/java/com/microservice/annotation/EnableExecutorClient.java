package com.microservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangwei
 * @date 2020-09-19
 * <p> 开启调度执行器功能
 * 1. 客户端自动重连功能             [已完成]
 * 2. 心跳检测功能                  [已完成]
 * 3. 自动消毁功能                  [已完成]
 * 4. 自动注册执行器功能             [已完成]
 * 5. 自动扫描任务功能               [已完成]
 * 6. 服务器集群功能                [已完成]
 * 7. 服务器重上线,加入集群          [已完成]
 * 8. 执行器集群功能,负载均衡         [已完成]
 * 9. cron表达式解析,特定时间执行     [进行中]
 * 10. 服务器上下线邮件通知           [未开始]
 * 11. 集群客户端通信能力             [已完成]
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableExecutorClient {
}
