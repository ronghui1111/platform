package com.cpirh.gateway;

import com.cpirh.redis.imports.RedisCoreImportConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 15:32
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@Slf4j
@Import({ RedisCoreImportConfigs.class})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
