package com.microservice.utils;

import com.microservice.protocol.BaseProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2020-09-14
 * <p> 协议转换工具
 */
@Slf4j
public class ProtoUtils {

    /**
     * 回调根路径
     */
    public static final String CALL_BACK_ROOT = "/callback";
    /**
     * 回调根路径下处理路径
     */
    public static final String CALL_BACK_HANDLER = "/handle";

    /**
     * 协议名获取协议
     *
     * @param type
     * @return
     */
    public static String protoToClass(String type) {
        try {
            if (Objects.isNull(type)) {
                return null;
            }

            if (type.length() <= 0) {
                return null;
            }

            byte[] items = type.getBytes();

            int i = (int) items[0];
            if (i < 97) {
                i = i - 65 + 97;
                items[0] = (byte) i;
                type = new String(items);
            }
            return type;
        } catch (NoSuchBeanDefinitionException ex) {
            log.error(">>> No agreement found: {}, error:", type, ex);
        }
        return null;
    }

    /**
     * 通过协议名 转 协议
     *
     * @param type
     * @param applicationContext
     * @return
     */
    public static BaseProtocol protocol(String type, ApplicationContext applicationContext) {
        String protoToClass = protoToClass(type);

        // 1.协议不存在
        if (StringUtils.isBlank(protoToClass)) {
            log.error(">>> 协议: {}, 不存在", type);
            return null;
        }

        Object bean = applicationContext.getBean(protoToClass);


        // 2.协议处理
        return (BaseProtocol) bean;
    }

    /**
     * 查询回调处理路径
     *
     * @return
     */
    public static String findCallBackUrl() {
        return CALL_BACK_ROOT + CALL_BACK_HANDLER;
    }

    /**
     * 通过 Consul注册获取随便一台服务器服务路径，通过指定路径
     *
     * @param applicationContext
     * @return
     */
    public static String findHostByServiceName(ApplicationContext applicationContext, String serviceId) {
        if (Objects.isNull(applicationContext) || StringUtils.isBlank(serviceId)) {
            throw new RuntimeException("--->>> ApplicationContext is empty!");
        }

        if (StringUtils.isBlank(serviceId)) {
            throw new RuntimeException("--->>> Consul serviceId is empty!");
        }
        ConsulDiscoveryClient consulDiscoveryClient = applicationContext.getBean(ConsulDiscoveryClient.class);
        List<ServiceInstance> allInstances = consulDiscoveryClient.getAllInstances();
        if (CollectionUtils.isEmpty(allInstances)) {
            throw new RuntimeException("--->>> Consul Registration list is empty!");
        }

        List<String> allowIps = allInstances.parallelStream()
                .filter(f -> Objects.deepEquals(f.getServiceId(), serviceId))
                .map(ServiceInstance::getHost)
                .distinct()
                .collect(Collectors.toList());

        int randomInt = RandomUtils.nextInt(0, allowIps.size() - 1);
        return allowIps.get(randomInt);
    }
}
