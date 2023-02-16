package com.cpirh.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 16:48
 */
@FeignClient(name = "biz", path = "/cpirh-biz")
public interface BizFeignClient {
    @GetMapping("/token/getUserInfo")
    Object getUserInfo();
}
