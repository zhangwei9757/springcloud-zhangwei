package com.zhangwei.utils;

import com.zhangwei.protocol.BaseProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-09-14
 * <p> 协议转换工具
 */
@Slf4j
public class ProtoUtils {

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

            if (type.length() <=0) {
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
        Object bean = applicationContext.getBean(protoToClass(type));

        // 1.协议不存在
        if (Objects.isNull(bean)) {
            log.error(">>> 协议: {}, 不存在", type);
        }

        // 2.协议处理
        return (BaseProtocol) bean;
    }
}
