package com.cpirh.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/21 15:26
 */
@ConfigurationProperties(prefix = "spring.redis")
@Component
@Data
public class RedisProperties {
    private String host;
    private Integer port;
    private String password;
    private Integer database;
}
