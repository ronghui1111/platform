package com.cpirh.authority.imports;

import com.cpirh.redis.imports.RedisCoreImportConfigs;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 16:21
 */
@ComponentScan("com.cpirh.user.core")
@Import(RedisCoreImportConfigs.class)
public class UserCoreImportConfigs {
}
