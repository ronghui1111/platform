package com.cpirh.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 17:41
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder().group("用户模块")
                .pathsToMatch("/**")
                .packagesToScan("com.cpirh.auth.controller").build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("电投融和平台用户系统接口文档")
                        .version("1.0")
                        .description("用户系统接口文档Knife4j文档")
                        .termsOfService("http://doc.xiaominfo.com")
                        .license(new License().name("Apache 2.0")
                                .url("http://doc.xiaominfo.com")));
    }


}
