package com.cpirh.gateway.authorization;

import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cpirh.common.bo.LoginDetailBo;
import com.cpirh.common.constants.AuthConstants;
import com.cpirh.common.response.ResponseModel;
import com.cpirh.common.utils.CpiRhBase64Utils;
import com.cpirh.gateway.config.SaTokenConfig;
import com.cpirh.gateway.handler.AuthorityRouterHandler;
import com.cpirh.gateway.handler.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;


/**
 * @author ronghui
 * @Description
 * @date 2023/2/13 19:44
 */
@Slf4j
@Component
public class TokenAuthorization extends SaReactorFilter {
    @Autowired
    private AuthorityRouterHandler authorityRouterHandler;
    @Autowired
    private SaTokenConfig saTokenConfig;
    private SupplierExt token = () -> {
    };
    private SupplierExt auth = () -> {
    };
    private ConsumerExt<String> permit = (obj) -> {
    };
    private Function<Throwable, ResponseModel> error = (e) -> ResponseModel.error();

    public TokenAuthorization setPermit(ConsumerExt<String> permit) {
        this.permit = permit;
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

    public TokenAuthorization setError(Function<Throwable, ResponseModel> error) {
        this.error = error;
        return this;
    }

    public TokenAuthorization() {
        // 拦截路由
        this
                // 校验登录
                .setAuth(StpUtil::checkLogin)
                // 校验权限
                .setPermit(StpUtil::checkPermission)
                // 将用户信息添加进请求头
                .setToken(this::addTokenHeader)
                // 异常处理函数
                .setError(GlobalExceptionHandler::handlerException);
    }

    private void addTokenHeader() {
        LoginDetailBo loginDetail = StpUtil.getTokenSession().getModel(AuthConstants.SA_TOKEN_DETAIL_KEY, LoginDetailBo.class);
        if (Objects.nonNull(loginDetail)) {
            String header = CpiRhBase64Utils.encode(loginDetail);
            log.debug("into addTokenHeader, {} : {}", AuthConstants.TOKEN_HEADER, header);
            ServerWebExchange context = SaReactorSyncHolder.getContext();
            ServerHttpRequest request = context.getRequest().mutate().headers((httpHeaders) -> {
                httpHeaders.add(AuthConstants.TOKEN_HEADER, header);
                httpHeaders.remove(saTokenConfig.getTokenName());
            }).build();
            SaReactorSyncHolder.setContext(context.mutate().request(request).build());
        } else {
            throw new NotBasicAuthException();
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
                List<String> includeList = authorityRouterHandler.getIncludeList();
                List<String> excludeList = authorityRouterHandler.getExcludeList();
                SaRouter.match(includeList).notMatch(excludeList).check((r) -> {
                    this.auth.andThen(permit).andThen(token).accept(url);
                });
                break label68;
            } catch (StopMatchException var10) {
                break label68;
            } catch (Throwable var11) {
                String result = var11 instanceof BackResultException ? var11.getMessage() : JSONUtil.toJsonStr(this.error.apply(var11));
                if (exchange.getResponse().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE) == null) {
                    exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
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
