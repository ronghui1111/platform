package com.cpirh.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 17:33
 */
@ConfigurationProperties(prefix = "sa-token")
@Component
@Data
public class SaTokenConfig {
    String tokenName;
}
