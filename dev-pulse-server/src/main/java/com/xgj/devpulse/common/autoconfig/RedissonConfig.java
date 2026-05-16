package com.xgj.devpulse.common.autoconfig;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 配置之后，执行Redisson时，将不通过Spring代理
// 这样可以彻底解决循环依赖问题
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);

        config.useSingleServer()
                .setAddress(address)
                .setPassword(password)
                .setDatabase(database)
                .setConnectionMinimumIdleSize(5)    // 最小连接数
                .setConnectionPoolSize(20);         // 最大连接数

        return Redisson.create(config);
    }
}
