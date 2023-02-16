package com.cpirh.biz.controller;

import com.cpirh.common.utils.TokenUtils;
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
public class TestToken {
    @GetMapping("getUserInfo")
    public Object getUserInfo() {
        return TokenUtils.getUserDetail();
    }
}
