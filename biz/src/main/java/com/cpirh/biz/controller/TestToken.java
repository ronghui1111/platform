package com.cpirh.biz.controller;

import com.cpirh.common.utils.TokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/14 10:41
 */
@RestController
@RequestMapping("token")
@Tag(name = "业务测试")
public class TestToken {
    @GetMapping("getUserInfo")
    @Operation(summary = "获取登录用户信息")
    public Object getUserInfo() {
        return TokenUtils.getUserDetail();
    }
}
