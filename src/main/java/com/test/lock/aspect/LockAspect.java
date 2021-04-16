package com.test.lock.aspect;

import com.test.lock.annotation.RedisLock;
import com.test.lock.utils.RedisLockHelper;
import com.test.lock.utils.JedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.lang.reflect.Method;
import java.util.UUID;


@Aspect
@Component
public class LockAspect {
     @Autowired
     private RedisLockHelper redisLockHelper;
     @Autowired
     private JedisUtil jedisUtil;

     Logger logger = LoggerFactory.getLogger(LockAspect.class);


     @Pointcut("@annotation(com.test.lock.annotation.RedisLock)")
     public void redisLock(){}

     @Around("redisLock()")
     public Object around(ProceedingJoinPoint joinPoint){
          //获取到 Jedis客户端
          Jedis jedis = jedisUtil.getJedis();
          //从切面对应方法的注解中获取锁的相关信息
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          Method method = signature.getMethod();
          //获取到注解
          RedisLock annotation = method.getAnnotation(RedisLock.class);
          String key = annotation.key();
          String uuid = UUID.randomUUID().toString();
          int expireTime = annotation.expire();

          //加锁操作
          try {
               boolean isLock = redisLockHelper.lock(jedis, key, uuid, expireTime, annotation.timeUnit());
               logger.info("isLock：",isLock);
               if(!isLock){
                    logger.error("----获取锁失败----");
                    throw  new RuntimeException("获取锁失败");
               }
               //否则获取成功，执行业务操作
               try {
                    return joinPoint.proceed();
               } catch (Throwable throwable) {
                    throw  new RuntimeException("系统异常");
               }
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               logger.info("----释放锁----");
               redisLockHelper.unlock(jedis,key,uuid);
               if(jedis != null)
                    jedis.close();
          }
          return null;
     }
}
