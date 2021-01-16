package com.microservice.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.microservice.bean.SchedulerConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020-10-02
 * <p>
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "microservice.scheduler", name = "swagger-enable", havingValue = "true")
public class SwaggerConfig {

    /**
     * Swagger2 自由扫描配置符
     */
    public static final String SPLITOR = ";";

    @Bean
    public Docket createRestApi(SchedulerConfigurationProperties properties) {

        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization")
                .description("Token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("bearer")
                .required(false)
                .build();
        pars.add(tokenPar.build());

        String basePackage = "com.microservice.controller" + SPLITOR + properties.getSwaggerBasePath();

        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(LocalDateTime.class, Date.class)
                .directModelSubstitute(LocalDateTime.class, String.class)
                .directModelSubstitute(LocalDateTime.class, String.class)
                .directModelSubstitute(ZonedDateTime.class, String.class)
                .apiInfo(new ApiInfoBuilder()
                        .title(properties.getSwaggerTitle())
                        .description(properties.getSwaggerDescription())
                        .termsOfServiceUrl(properties.getSwaggerTermsOfServiceUrl())
                        .contact(new Contact(
                                        properties.getSwaggerName(),
                                        properties.getSwaggerUrl(),
                                        properties.getSwaggerEmail()
                                )
                        )
                        .version(properties.getSwaggerVersion())
                        .license(properties.getSwaggerLicense())
                        .licenseUrl(properties.getSwaggerLicenseUrl())
                        .build())
                .select()
                .apis(basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            for (String strPackage : basePackage.split(SPLITOR)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }
}