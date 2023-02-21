package com.cpirh.user.core.config;

import com.cpirh.user.core.interceptor.HttpRequestTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 15:12
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private HttpRequestTokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns("/**/acc/login");
    }
}
