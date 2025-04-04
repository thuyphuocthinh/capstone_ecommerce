package com.tpt.capstone_ecommerce.ecommerce.config.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisTestConnection {
    @Bean
    public CommandLineRunner testRedisConnection(JedisPool jedisPool) {
        return args -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set("testKey", "Hello Redis!");
                String value = jedis.get("testKey");
                System.out.println("Redis test value: " + value);
            } catch (Exception e) {
                System.err.println("Redis connection failed: " + e.getMessage());
            }
        };
    }
}
