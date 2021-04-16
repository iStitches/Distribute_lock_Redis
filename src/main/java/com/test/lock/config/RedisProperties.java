package com.test.lock.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis.config")
public class RedisProperties {
    private String host;
    private int port;
    private String password;
    private int max_idle;
    private int max_wait;
    private int timeout;
    private int retry_num;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMax_idle() {
        return max_idle;
    }

    public void setMax_idle(int max_idle) {
        this.max_idle = max_idle;
    }

    public int getMax_wait() {
        return max_wait;
    }

    public void setMax_wait(int max_wait) {
        this.max_wait = max_wait;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetry_num() {
        return retry_num;
    }

    public void setRetry_num(int retry_num) {
        this.retry_num = retry_num;
    }
}
