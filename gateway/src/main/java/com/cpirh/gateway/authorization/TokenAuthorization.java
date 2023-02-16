package com.cpirh.gateway.authorization;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.cpirh.common.constants.AuthConstants;
import com.cpirh.common.utils.CpiRhBase64Utils;
import com.cpirh.gateway.config.SaTokenConfig;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public Function<Object, Object> permit = (obj) -> null;
    public SupplierExt<Object> auth = () -> null;
    public Consumer<Object> token = (obj) -> {
    };

    public TokenAuthorization setPermit(Function<Object, Object> permit) {
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
    public TokenAuthorization setAuth(SupplierExt<Object> auth) {
        this.auth = auth;
        return this;
    }

    public TokenAuthorization setToken(Consumer<Object> token) {
        this.token = token;
        return this;
    }

    private List<String> excludes = Lists.newArrayList("/favicon.ico", "/**/webjars/**", "/**/doc.html", "/**/swagger-resources/**", "/**/v3/api-docs/**");


    public TokenAuthorization() {
        // 拦截所有path
        this.addInclude("/**")
                // 指定 [放行路由]
                .setExcludeList(excludes)
                .addExclude(AuthConstants.LOGIN_URL)
                // 指定[认证函数]: 每次请求执行
                .setAuth(this::validLogin).setPermit(this::validPermit).setToken(this::addToken2Header)
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> errors(e));
    }

    private Object validLogin() {
        log.debug("into validLogin");
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if (tokenInfo == null) {
            throw NotLoginException.newInstance(null, NotLoginException.NOT_TOKEN, null);
        }
        if (!tokenInfo.isLogin) {
            throw NotLoginException.newInstance(null, NotLoginException.TOKEN_TIMEOUT, tokenInfo.getTokenValue());
        }
        Object object = StpUtil.getTokenSession().get(tokenInfo.getLoginId().toString());
        log.info("current login user info :{}", object);
        if (Objects.isNull(object)) {
            throw NotLoginException.newInstance(null, NotLoginException.INVALID_TOKEN, tokenInfo.getTokenValue());
        }
        return object;
    }

    private Object validPermit(Object obj) {
        log.debug("into validPermit");
        return obj;
    }

    private void addToken2Header(Object obj) {
        String json = CpiRhBase64Utils.encode(obj);
        log.debug("into addToken2Header, {} : {}", AuthConstants.TOKEN_HEADER, json);
        ServerWebExchange context = SaReactorSyncHolder.getContext();
        if (Objects.nonNull(obj)) {
            ServerHttpRequest request = context.getRequest().mutate().header(AuthConstants.TOKEN_HEADER, json).headers((httpHeaders) -> {
                httpHeaders.remove(saTokenConfig.getTokenName());
            }).build();
            SaReactorSyncHolder.setContext(context.mutate().request(request).build());
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
                SaRouter.match(super.getIncludeList()).notMatch(super.getExcludeList()).check((r) -> {
                    this.beforeAuth.run(null);
                    this.auth.andThen(permit).end(token).accept(null);
                });
                break label68;
            } catch (StopMatchException var10) {
                break label68;
            } catch (Throwable var11) {
                String result = var11 instanceof BackResultException ? var11.getMessage() : String.valueOf(this.error.run(var11));
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

    interface SupplierExt<T> extends Supplier<T> {
        @Override
        T get();

        default SupplierExt<T> andThen(Function<? super T, ? extends T> after) {
            Objects.requireNonNull(after);
            return () -> after.apply(get());
        }

        default Consumer<T> end(Consumer<T> end) {
            Objects.requireNonNull(end);
            return (u) -> end.accept(this.get());
        }
    }
}
