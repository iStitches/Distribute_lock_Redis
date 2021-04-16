package com.test.lock.utils;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockHelper {
    //解决超时等待，每 sleepTime检查一次是否等待超时
    private long sleepTime = 100;

    /**
     * 指定锁的有效时间并添加分布式锁
     * set key value [EX seconds][PX milliseconds] [NX|XX] 添加分布式锁
     * @param jedis
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public boolean lock(Jedis jedis, String key, String value, int timeout, TimeUnit timeUnit){
        long seconds = timeUnit.toSeconds(timeout);
        SetParams setParams = new SetParams();
        setParams.ex((int) seconds);
        setParams.nx();
        return "OK".equals(jedis.set(key,value,setParams));
    }

    /**
     * lua脚本 + setnx + expire 进行加锁
     * @param jedis
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean lock_with_lua(Jedis jedis,String key,String value,int seconds){
        String lua_scripts = "if redis.call('setnx',KEYS[1],ARGV[1]) == 1 then" +
                "redis.call('expire',KEYS[1],ARGV[2]) return 1 else return 0 end";
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        keys.add(key);
        values.add(value);
        values.add(String.valueOf(seconds));
        Object result = jedis.eval(lua_scripts, keys, values);
        //判断是否成功
        return result.equals(1L);
    }

    /**
     * 加锁同时设置等待超时时间 waitTime
     * @param jedis
     * @param key
     * @param value
     * @param timeout
     * @param waitTime
     * @param timeUnit
     * @return
     */
    public boolean lock_with_waitTime(Jedis jedis,String key,String value,int timeout,long waitTime,TimeUnit timeUnit) throws InterruptedException {
        long seconds = timeUnit.toSeconds(timeout);
        while(waitTime > 0){
            SetParams params = new SetParams();
            params.nx();
            params.ex((int)seconds);
            if("OK".equals(jedis.set(key,value,params))){
                 return true;
            }
            waitTime -= sleepTime;
            Thread.sleep(sleepTime);
        }
        return false;
    }

    /**
     * lua脚本 + del 解锁
     * @param jedis
     * @param key
     * @param value
     * @return
     */
    public boolean unlock(Jedis jedis,String key,String value) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                "return redis.call('del',KEYS[1]) else return 0 end";
        return jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(value)).equals(1L);
    }
}
