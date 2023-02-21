package com.cpirh.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.json.JSONUtil;
import com.cpirh.auth.feign.BizFeignClient;
import com.cpirh.common.bo.LoginDetailBo;
import com.cpirh.common.utils.TokenUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.cpirh.common.constants.AuthConstants.*;

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
    public Object login(@RequestParam String account, @RequestParam String password) {
        LoginDetailBo loginInfo = new LoginDetailBo();
        if ("ronghui".equalsIgnoreCase(account) && "Rh950831.".equalsIgnoreCase(password)) {
            StpUtil.login(account);
            loginInfo.setUserId(0);
            loginInfo.setName("戎辉");
            loginInfo.setMobile("15695555302");
            loginInfo.setAccount(account);
            StpUtil.getTokenSession()
                    .set(SA_TOKEN_DETAIL_KEY, loginInfo).set(SA_TOKEN_ROLE_KEY, Lists.newArrayList(""))
                    .set(SA_TOKEN_PERMISSION_KEY, Lists.newArrayList(""));
            return SaResult.ok("登录成功");
        } else if ("admin".equalsIgnoreCase(account) && "Rh950831.".equalsIgnoreCase(password)) {
            StpUtil.login(account);
            loginInfo.setUserId(1);
            loginInfo.setName("ADMIN");
            loginInfo.setMobile("15695555302");
            loginInfo.setAccount(account);
            StpUtil.getTokenSession().set(SA_TOKEN_DETAIL_KEY, loginInfo).set(SA_TOKEN_ROLE_KEY, Lists.newArrayList("")).set(SA_TOKEN_PERMISSION_KEY, Lists.newArrayList(""));
            return SaResult.ok("登录成功");
        }
        return SaResult.error("账户或密码错误");
    }

    @GetMapping("user/isLogin")
    @ApiOperation("检测是否登录")
    public SaResult isLogin() {
        LoginDetailBo userDetail = TokenUtils.getUserDetail();
        log.info("user:{}", JSONUtil.toJsonStr(userDetail));
        if (Objects.nonNull(userDetail)) {
            return SaResult.data(userDetail);
        } else {
            return SaResult.error("未登录");
        }
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
