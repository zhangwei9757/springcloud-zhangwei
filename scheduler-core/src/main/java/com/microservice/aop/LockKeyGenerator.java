package com.microservice.aop;

import com.microservice.annotation.CacheLock;
import com.microservice.annotation.CacheParam;
import com.microservice.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020-02-16
 * <p>
 * redis 分布式锁 key 生成器
 */
@Slf4j
@Component
public class LockKeyGenerator implements CacheKeyGenerator {

    @Override
    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        CacheLock lockAnnotation = method.getAnnotation(CacheLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();

        StringBuilder builder = new StringBuilder();

        CacheParam targetAnnotation = null;
        int index = -1;

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            final CacheParam annotation = parameter.getAnnotation(CacheParam.class);
            if (annotation == null) {
                continue;
            }
            index = i;
            targetAnnotation = annotation;
        }

        if (null == targetAnnotation) {
            return Constants.DUPLICATE_LOCK + lockAnnotation.prefix() + lockAnnotation.delimiter() + method.getName();
        }

        // CacheLock 注解添加方法是否存在参数指定
        String targetVal = targetAnnotation.value();
        String parameterName = parameters[index].getName();

        if (StringUtils.isBlank(targetVal)) {
            String error = String.format("%s %s not found CacheParam annotation value", method, parameterName);
            log.error("--->>> {}", error);
        }

        // 目标寻找参数名与当前参数名一致，表示寻找到了
        if (Objects.deepEquals(parameterName, targetVal)) {
            parameters[index].getName();
            Object arg = args[index];
            builder.append(lockAnnotation.delimiter())
                    .append(arg);
        } else {
            // 优先使用此指定条件
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String fieldName = field.getName();
                    boolean allow = Objects.deepEquals(fieldName, targetVal);
                    if (!allow) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object fieldVal = ReflectionUtils.getField(field, object);
                    builder.append(lockAnnotation.delimiter())
                            .append(fieldVal);
                }
            }
        }

        return Constants.DUPLICATE_LOCK + lockAnnotation.prefix() + lockAnnotation.delimiter() + targetVal + builder.toString();
    }
}
