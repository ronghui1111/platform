package com.cpirh.user.core.config;

import cn.hutool.core.lang.reflect.MethodHandleUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.cpirh.common.constants.StringConstants;
import com.cpirh.user.core.annotations.AuthorityIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sun.reflect.misc.MethodUtil;

import java.lang.reflect.Method;
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
    @Autowired
    RequestMappingHandlerMapping mapping;


    @Override
    public void run(ApplicationArguments args) {
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        Optional.of(methodMap).ifPresent(map -> map.forEach((methodInfo, value) -> {
            Class<?> beanType = value.getBeanType();
            PatternsRequestCondition patternsCondition = methodInfo.getPatternsCondition();
            // 收集接口信息（只收集basePackage下的接口）
            if (!patternsCondition.isEmptyPathMapping() && beanType.getCanonicalName().startsWith(basePackage) && ignore(value)) {
                patternsCondition.getPatterns().stream().filter(StrUtil::isNotEmpty).forEach(url -> {
                    Api menu = AnnotationUtils.findAnnotation(beanType, Api.class);
                    ApiOperation card = AnnotationUtils.findAnnotation(value.getMethod(), ApiOperation.class);
                    String menuComment = Optional.ofNullable(menu).filter(Objects::nonNull).map(Api::tags).filter(ArrayUtil::isNotEmpty).map(es -> es[0]).orElse(StringConstants.EMPTY);
                    String cardComment = Optional.ofNullable(card).filter(Objects::nonNull).map(ApiOperation::value).orElse(StringConstants.EMPTY);
                    log.info("menu:{},card:{},url:{}",menuComment,cardComment,url);
                });
            }
        }));
    }

    private boolean ignore(HandlerMethod mapping) {
        return !AnnotationUtils.isAnnotationDeclaredLocally(AuthorityIgnore.class, mapping.getBeanType()) && Objects.isNull(AnnotationUtils.findAnnotation(mapping.getMethod(), AuthorityIgnore.class));
    }
}
