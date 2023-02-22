package com.cpirh.authority.config;

import com.cpirh.authority.interceptor.HttpRequestTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.cpirh.common.constants.AuthConstants.LOGIN_URL;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 15:12
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    //    @Value("${server.base-package}")
//    private String basePackage;
//    @Value("${server.servlet.context-path}")
//    private String contextPath;
    @Autowired
    private HttpRequestTokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns("/**" + LOGIN_URL);
    }

}
