package com.cpirh.authority.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 16:12
 */
@Slf4j
@Component
public class FeignRequestTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            log.info("feign request url:{},request:{}", requestTemplate.request().url(), JSONUtil.toJsonStr(requestTemplate.request().headers()));
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                requestTemplate.header(header, request.getHeader(header));
            }
            log.info("feign request url:{},request:{}", requestTemplate.request().url(), JSONUtil.toJsonStr(requestTemplate.request().headers()));
        } catch (Exception e) {
            log.error("feign request url:{},request:{} error", requestTemplate.request().url(), JSONUtil.toJsonStr(requestTemplate.request().headers()), e);
        }
    }
}
