package com.cpirh.redis.aspect;

import com.cpirh.redis.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/21 15:01
 */
@Aspect
@Component
@Slf4j
public class RedisLockAspect {

    @Autowired
    private RedissonClient redissonClient;

    private static final String REDIS_PREFIX = "redisson:lock:%s:%s";

    @Pointcut("@annotation(com.cpirh.redis.annotation.RedisLock)")
    public void pointcut() {
    }
    @Around("pointcut() && @annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        log.info("redis key" + redisLock.key());
        String key = redisLock.key();
        String identify = redisLock.identify();
        RLock rLock = redissonClient.getLock(String.format(REDIS_PREFIX, key, identify));
        rLock.lock(redisLock.expireSeconds(), TimeUnit.SECONDS);
        Object result = null;
        try {
            //执行方法
            result = joinPoint.proceed();
        } finally {
            rLock.unlock();
        }
        return result;
    }
}
