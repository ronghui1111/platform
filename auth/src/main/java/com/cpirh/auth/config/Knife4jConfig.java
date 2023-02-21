package com.cpirh.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 17:41
 */
@Configuration
public class Knife4jConfig {


    @Bean
    public Docket Api3() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                //分组名称
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.cpirh.auth.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户模块接口文档")
                .description("用户模块接口文档")
                .version("1.0")
                .build();
    }
}
