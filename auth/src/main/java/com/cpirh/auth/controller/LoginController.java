package com.cpirh.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.json.JSONUtil;
import com.cpirh.auth.feign.BizFeignClient;
import com.cpirh.common.utils.TokenUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 17:10
 */
@RestController
@Api(tags = "登录相关接口")
@Slf4j
public class LoginController {
    @Autowired
    private BizFeignClient bizFeignClient;

    @PostMapping("user/login")
    @ApiOperation("登录接口")
    public Object login(@RequestParam String userName, @RequestParam String password) {
        if ("ronghui".equalsIgnoreCase(userName) && "Rh950831.".equalsIgnoreCase(password)) {
            StpUtil.login(userName);
            Map<String, Object> userInfo = Maps.newHashMap();
            userInfo.put("userName", userName);
            userInfo.put("phone", "15695555302");
            userInfo.put("name", "戎辉");
            StpUtil.getTokenSession().set(userName, JSONUtil.toJsonStr(userInfo));
            //TODO 返回用户信息
            return SaResult.ok("登录成功");
        }
        return SaResult.error("账户或密码错误");
    }

    @GetMapping("user/isLogin")
    @ApiOperation("检测是否登录")
    public SaResult isLogin() {
        log.info("user:{}", JSONUtil.toJsonStr(TokenUtils.getUserDetail()));
        if (StpUtil.isLogin()) {
            return SaResult.data(StpUtil.getTokenInfo());
        } else {
            return SaResult.error("未登录");
        }
    }

    @GetMapping("user/tokenInfo")
    @ApiOperation("获取token信息")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    @GetMapping("user/logout")
    @ApiOperation("登出接口")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    @GetMapping("user/userInfo")
    @ApiOperation("获取userInfo信息")
    public Object userInfo() {
        return bizFeignClient.getUserInfo();
    }

}
