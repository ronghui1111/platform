package com.cpirh.authority.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.cpirh.authority.annotations.AuthorityIgnore;
import com.cpirh.common.constants.StringConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/16 13:40
 */
@Configuration
@Slf4j
public class RequestApiCollector implements ApplicationRunner {
    @Value("${server.base-package}")
    private String basePackage;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RequestMappingHandlerMapping mapping;


    @Override
    public void run(ApplicationArguments args) {
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        Optional.of(methodMap).ifPresent(map -> map.forEach((methodInfo, value) -> {
            Class<?> beanType = value.getBeanType();
            Set<String> patternValues = methodInfo.getPatternValues();
            // 收集接口信息（只收集basePackage下的接口）
            if (CollUtil.isNotEmpty(patternValues)) {
                if (beanType.getCanonicalName().startsWith(basePackage) && ignore(value)) {
                    patternValues.stream().filter(StrUtil::isNotEmpty).forEach(url -> {
                        Tag menu = AnnotationUtils.findAnnotation(beanType, Tag.class);
                        Operation card = AnnotationUtils.findAnnotation(value.getMethod(), Operation.class);
                        String menuComment = Optional.ofNullable(menu).filter(Objects::nonNull).map(Tag::description).filter(ArrayUtil::isNotEmpty).orElse(StringConstants.EMPTY);
                        String cardComment = Optional.ofNullable(card).filter(Objects::nonNull).map(Operation::summary).orElse(StringConstants.EMPTY);
                        url = contextPath + url;
                        log.info("menu:{},card:{},url:{}", menuComment, cardComment, url);
                    });
                }
            }
        }));
    }

    private boolean ignore(HandlerMethod mapping) {
        return !AnnotationUtils.isAnnotationDeclaredLocally(AuthorityIgnore.class, mapping.getBeanType()) && Objects.isNull(AnnotationUtils.findAnnotation(mapping.getMethod(), AuthorityIgnore.class));
    }
}
