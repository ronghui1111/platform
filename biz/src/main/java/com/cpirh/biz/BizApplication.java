package com.cpirh.biz;

import com.cpirh.user.core.imports.UserCoreImportConfigs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/14 10:19
 */
@SpringBootApplication
@Import(UserCoreImportConfigs.class)
public class BizApplication {
    public static void main(String[] args) {
        SpringApplication.run(BizApplication.class, args);
    }
}
