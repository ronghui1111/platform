package com.cpirh.common.utils;

import com.cpirh.common.bo.LoginDetailBo;

import java.util.Objects;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 14:47
 */
public class TokenUtils {
    private static final ThreadLocal<LoginDetailBo> userDetail = new ThreadLocal<>();

    public static LoginDetailBo getUserDetail() {
        LoginDetailBo o = userDetail.get();
        if (Objects.isNull(o)) {
            throw new RuntimeException("未获取到当前用户信息");
        }
        return o;
    }

    public static void setUserDetail(LoginDetailBo o) {
        userDetail.set(o);
    }

    public static void removeUserDetail() {
        userDetail.remove();
    }
}
