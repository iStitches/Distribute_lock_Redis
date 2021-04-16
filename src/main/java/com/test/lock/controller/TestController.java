package com.test.lock.controller;

import com.test.lock.annotation.RedisLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/index")
    @RedisLock(key = "REDIS_LOCK")
    public String index(){
        return "hello-world";
    }
}
