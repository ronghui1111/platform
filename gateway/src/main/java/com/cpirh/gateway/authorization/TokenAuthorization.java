package com.cpirh.gateway.authorization;

import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cpirh.common.bo.LoginDetailBo;
import com.cpirh.common.constants.AuthConstants;
import com.cpirh.common.utils.CpiRhBase64Utils;
import com.cpirh.gateway.config.SaTokenConfig;
import com.cpirh.gateway.handler.GlobalExceptionHandler;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.cpirh.common.constants.AuthConstants.SA_TOKEN_DETAIL_KEY;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 19:44
 */
@Slf4j
@Component
public class TokenAuthorization extends SaReactorFilter {
    @Autowired
    private SaTokenConfig saTokenConfig;
    public SupplierExt token = () -> {
    };
    public SupplierExt auth = () -> {
    };
    public ConsumerExt<String> permit = (obj) -> {
    };

    public TokenAuthorization setPermit(ConsumerExt<String> permit) {
        this.permit = permit;
        return this;
    }

    public TokenAuthorization addInclude(String... paths) {
        super.getIncludeList().addAll(Arrays.asList(paths));
        return this;
    }

    public TokenAuthorization setExcludeList(List<String> pathList) {
        super.setExcludeList(pathList);
        return this;
    }

    public TokenAuthorization addExclude(String... paths) {
        super.getExcludeList().addAll(Arrays.asList(paths));
        return this;
    }

    public TokenAuthorization setAuth(SupplierExt auth) {
        this.auth = auth;
        return this;
    }

    public TokenAuthorization setToken(SupplierExt token) {
        this.token = token;
        return this;
    }

    private List<String> excludes = Lists.newArrayList("/favicon.ico", "/**/webjars/**", "/**/doc.html", "/**/swagger-resources/**", "/**/v3/api-docs/**");


    public TokenAuthorization() {
        // 拦截所有path
        this.addInclude("/**")
                // 指定 [放行路由]
                .setExcludeList(excludes).addExclude(AuthConstants.LOGIN_URL)
                // 指定[认证函数]: 每次请求执行
                .setAuth(StpUtil::checkLogin).setPermit(StpUtil::checkPermission).setToken(this::addTokenHeader)
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(GlobalExceptionHandler::handlerException);
    }
    private void addTokenHeader() {
        LoginDetailBo loginDetail = StpUtil.getTokenSession().getModel(SA_TOKEN_DETAIL_KEY, LoginDetailBo.class);
        if (Objects.nonNull(loginDetail)) {
            String header = CpiRhBase64Utils.encode(loginDetail);
            log.debug("into addToken2Header, {} : {}", AuthConstants.TOKEN_HEADER, header);
            ServerWebExchange context = SaReactorSyncHolder.getContext();
            ServerHttpRequest request = context.getRequest().mutate().header(AuthConstants.TOKEN_HEADER, header).headers((httpHeaders) -> {
                httpHeaders.remove(saTokenConfig.getTokenName());
            }).build();
            SaReactorSyncHolder.setContext(context.mutate().request(request).build());
        } else {
            throw new NotBasicAuthException();
        }
    }

    private SaResult errors(Throwable e) {
        if (e instanceof NotLoginException) {
            return SaResult.get(((NotLoginException) e).getCode(), e.getMessage(), null);
        } else {
            return SaResult.error(e.getMessage());
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getAttributes().put("WEB_FILTER_CHAIN_KEY", chain);
        label68:
        {
            Mono var5;
            try {
                SaReactorSyncHolder.setContext(exchange);
                String url = Optional.ofNullable(exchange.getRequest()).map(es -> es.getPath()).map(es -> es.toString()).orElse(null);
                if (StrUtil.isEmpty(url)) {
                    break label68;
                }
                SaRouter.match(super.getIncludeList()).notMatch(super.getExcludeList()).check((r) -> {
                    this.beforeAuth.run(null);
                    this.auth.andThen(permit).andThen(token).accept(url);
                });
                break label68;
            } catch (StopMatchException var10) {
                break label68;
            } catch (Throwable var11) {
                String result = var11 instanceof BackResultException ? var11.getMessage() : JSONUtil.toJsonStr(this.error.run(var11));
                if (exchange.getResponse().getHeaders().getFirst("Content-Type") == null) {
                    exchange.getResponse().getHeaders().set("Content-Type", "text/plain; charset=utf-8");
                }
                var5 = exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
            } finally {
                SaReactorSyncHolder.clearContext();
            }
            return var5;
        }
        SaReactorSyncHolder.setContext(exchange);
        return chain.filter(exchange).doFinally((r) -> {
            SaReactorSyncHolder.clearContext();
        });
    }

    interface SupplierExt {
        void run();

        default <T> ConsumerExt<T> andThen(ConsumerExt<T> after) {
            Objects.requireNonNull(after);
            return (u) -> {
                run();
                after.accept(u);
            };
        }
    }

    interface ConsumerExt<T> {
        void accept(T t);

        default ConsumerExt<T> andThen(SupplierExt after) {
            Objects.requireNonNull(after);
            return (t) -> {
                accept(t);
                after.run();
            };
        }

    }
}
