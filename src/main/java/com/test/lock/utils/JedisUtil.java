package com.test.lock.utils;


import com.test.lock.config.RedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class JedisUtil {
    @Autowired
    RedisProperties redisProperties;

    private Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    ConcurrentHashMap<String, JedisPool> map = new ConcurrentHashMap<>();

    /**
     * 获取Jedis 线程池
     * @return
     */
    private JedisPool getJedisPool(){
        String key = redisProperties.getHost()+":"+redisProperties.getHost();
        JedisPool jedisPool = null;

        if(!map.containsKey(key)){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(redisProperties.getMax_idle());
            config.setMaxWaitMillis(redisProperties.getMax_wait());
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            jedisPool = new JedisPool(config,redisProperties.getHost(),redisProperties.getPort(),redisProperties.getTimeout(),redisProperties.getPassword());

            map.put(key,jedisPool);
        }
        else
           return map.get(key);
        return jedisPool;
    }

    /**
     * 获取 Jedis客户端
     * @return
     */
    public Jedis getJedis(){
        Jedis jedis = null;
        int count = 0;
        do{
            count++;
            try {
                jedis = getJedisPool().getResource();
            } catch (Exception e) {
                logger.error("get jedis failed....");
                if(jedis != null)
                    jedis.close();
            }
        }while(count < redisProperties.getRetry_num() && jedis==null);
        return jedis;
    }

}
