package com.cpirh.gateway.filter;

import cn.hutool.core.date.DateUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author ronghui
 * @Description
 * @date 2023/2/14 13:49
 */
@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {

    private static final String BEGIN_TIME = "beginTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(BEGIN_TIME, DateUtil.current());
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(BEGIN_TIME);
                    if (startTime != null) {
                        log.info("请求信息：[host: {}. URL: {}. 响应时间：{}.]", exchange.getRequest().getURI().getHost(), exchange.getRequest().getURI().getPath(), (DateUtil.current() - startTime) + "ms");
                    }
                })
        );
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
