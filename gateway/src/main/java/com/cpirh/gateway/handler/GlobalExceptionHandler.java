package com.cpirh.gateway.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.cpirh.common.response.ResponseModel;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/21 9:19
 */
public class GlobalExceptionHandler {

    public static ResponseModel handlerException(Throwable e) {
        // 打印堆栈，以供调试
        if (e instanceof NotLoginException) {
            return notLoginError((NotLoginException) e);
        } else if (e instanceof NotPermissionException) {
            return notPermitError((NotPermissionException) e);
        }
        return ResponseModel.error(e.getMessage());
    }

    static ResponseModel notLoginError(NotLoginException e) {
        // 判断场景值，定制化异常信息
        String message = "";
        int code;
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            code = 400;
            message = "用户未登录，请先登录！";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            code = 401;
            message = "当前登录无效！";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            code = 401;
            message = "当前登录已失效，请重新登陆！";
        } else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
            code = 403;
            message = "当前登录已被顶下线";
        } else if (e.getType().equals(NotLoginException.KICK_OUT)) {
            code = 403;
            message = "当前登录已被踢下线";
        } else {
            code = 401;
            message = "当前会话未登录";
        }
        // 返回给前端
        return ResponseModel.error(code, message);
    }

    static ResponseModel notPermitError(NotPermissionException e) {
        return ResponseModel.error(401, "当前用户无此权限：" + e.getPermission());
    }

}
