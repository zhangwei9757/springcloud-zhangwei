package com.microservice.annotations;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用数据库
 *
 * @author zhangwei
 * @date 2022-06-20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MapperScan(basePackages = "com.microservice.mapper")
@Import({
        MybatisPlusAutoConfiguration.class,
        MybatisPlusLanguageDriverAutoConfiguration.class,
        DynamicDataSourceAutoConfiguration.class
})
public @interface EnableMybatisPlusMapperScan {
}
