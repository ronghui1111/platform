package com.cpirh.gateway.handler;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.cpirh.common.constants.AuthConstants;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/20 15:35
 */
@Service
public class AuthorityPermitHandler implements StpInterface {
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return (List<String>) StpUtil.getTokenSession().get(AuthConstants.SA_TOKEN_PERMISSION_KEY);
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return (List<String>) StpUtil.getTokenSession().get(AuthConstants.SA_TOKEN_ROLE_KEY);
    }
}
