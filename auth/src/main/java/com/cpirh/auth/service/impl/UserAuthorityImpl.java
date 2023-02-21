package com.cpirh.auth.service.impl;

import cn.dev33.satoken.stp.StpInterface;

import java.util.List;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/17 17:45
 */
public class UserAuthorityImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return null;
    }
}
