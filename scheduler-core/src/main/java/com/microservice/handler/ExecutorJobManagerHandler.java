package com.microservice.handler;

import com.microservice.annotation.EnableExecutorClient;
import com.microservice.annotation.ExecutorJob;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.ExecutorJobMessage;
import com.microservice.dto.ReturnT;
import com.microservice.enums.ProtocolStausEnum;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.PingPongUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-09-20
 * <p>
 */
@Component
@ConditionalOnBean(annotation = EnableExecutorClient.class)
@Slf4j
public class ExecutorJobManagerHandler {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 注册执行器 applicationName + ip + no
     *
     * @param channel
     */
    public void registerExecutor(Channel channel, SchedulerConfigurationProperties properties) {
        if (Objects.isNull(properties)) {
            throw new RuntimeException("register properties is null or empty...");
        }

        SocketAddress socketAddress = channel.localAddress();
        String address = socketAddress.toString();
        String[] split = address.split(":");

        ExecutorJobMessage jobDto = new ExecutorJobMessage();
        jobDto.setHost(split[0]);
        jobDto.setPort(split[1]);
        jobDto.setProperties(properties);
        String json = JsonUtils.toJson(jobDto);

        assert json != null;
        MessageProtocolPoJo.MessageProtocol messageProtocol = MessageProtocolPoJo.MessageProtocol
                .newBuilder()
                // 写死协议名即可，开发对应关系创建即注定了
                .setProtocol(ProtocolStausEnum.registerRequest.name())
                .setLen(json.getBytes(PingPongUtils.CHARSET).length)
                .setContent(json)
                .build();

        channel.writeAndFlush(messageProtocol);
        log.info(">>> registerExecutor 自动注册执行器成功, 执行器信息[{}]", address);
    }


    /**
     * 执行器初次运行时加载所有 executorJob 注解任务
     */
    public void initExecutorJobHandlerMethodRepository() {
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);

        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, ExecutorJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<ExecutorJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, ExecutorJob.class));

            if (annotatedMethods.isEmpty()) {
                continue;
            }

            for (Map.Entry<Method, ExecutorJob> methodExecutorJobEntry : annotatedMethods.entrySet()) {
                Method method = methodExecutorJobEntry.getKey();
                ExecutorJob executorJob = methodExecutorJobEntry.getValue();

                if (Objects.isNull(executorJob)) {
                    continue;
                }

                String jobName = executorJob.value();
                if (jobName.trim().length() == 0) {
                    throw new RuntimeException("ExecutorJob method-jobhandler jobName invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                }

                // TODO 从数据库获取此执行器名是否真实存在, 进行校验

                // execute method
                if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
                    throw new RuntimeException("xxl-job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                    throw new RuntimeException("xxl-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                method.setAccessible(true);

                // init and destory
                Method initMethod = null;
                Method destroyMethod = null;

                if (executorJob.init().trim().length() > 0) {
                    try {
                        initMethod = bean.getClass().getDeclaredMethod(executorJob.init());
                        initMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }
                if (executorJob.destroy().trim().length() > 0) {
                    try {
                        destroyMethod = bean.getClass().getDeclaredMethod(executorJob.destroy());
                        destroyMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }

                try {
                    Object invoke = method.invoke(bean, "demo");
                    System.out.println(invoke);
                    if (Objects.nonNull(initMethod)) {
                        Object invoke1 = initMethod.invoke(bean);
                        System.out.println(invoke1);
                    }
                    if (Objects.nonNull(destroyMethod)) {
                        Object invoke1 = destroyMethod.invoke(bean);
                        System.out.println(invoke1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // registry jobhandler
                DefaultExecutorJobHolder.registExecutorJobHandler(jobName, new MethodJobHandler(bean, method, initMethod, destroyMethod));
            }
        }
    }
}
