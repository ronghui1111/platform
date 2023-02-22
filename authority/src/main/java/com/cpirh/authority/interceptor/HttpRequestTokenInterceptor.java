package com.cpirh.authority.interceptor;

import cn.hutool.core.util.StrUtil;
import com.cpirh.common.constants.AuthConstants;
import com.cpirh.common.bo.LoginDetailBo;
import com.cpirh.common.utils.CpiRhBase64Utils;
import com.cpirh.common.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/14 17:07
 */
@Slf4j
@Component
public class HttpRequestTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String value = request.getHeader(AuthConstants.TOKEN_HEADER);
            log.info("current user detail:{}", value);
            if (StrUtil.isNotBlank(value)) {
                log.info("current user detail set in threadLocal", value);
                TokenUtils.setUserDetail(CpiRhBase64Utils.decode(value, LoginDetailBo.class));
            }
        } catch (Exception e) {
            log.error("get current user detail error", e);
        } finally {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            log.info("current user detail remove from threadLocal");
            TokenUtils.removeUserDetail();
        } catch (Exception e) {
            log.error("current user detail remove from threadLocal error", e);
        } finally {
            HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        }
    }
}
