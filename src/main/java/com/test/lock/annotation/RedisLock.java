package com.test.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RedisLock {

    /**
     * 分布式锁的 key
     */
    String key();

    /**
     * 锁的过期时间，默认5秒
     * @return
     */
    int expire() default 5;

    /**
     * 尝试加锁，最多等待时间
     * @return
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 锁的超时时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
