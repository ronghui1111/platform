package com.cpirh.redis.annotation;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisLock {
    String key() default "";

    String identify() default "";

    int expireSeconds() default 180;
}
