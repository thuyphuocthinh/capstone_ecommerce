package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.redis.repository.CacheJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@RequiredArgsConstructor
public class CacheJWTImpl implements CacheJWT {
    private final JedisPool jedisPool;

    @Override
    public void setAccessToken(String key, String accessToken, int expiresIn) {
        try(Jedis jedis = jedisPool.getResource()) {
            // key: ecommerce:accessToken:asdf-a45sdf-er6asdf-asdf:78974654534
            // key: ecommerce:accessToken:[user_id]:[timestamp]
            jedis.setex(key.getBytes(), expiresIn, accessToken.getBytes());
        }
    }

    @Override
    public String getAccessToken(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public void deleteAccessToken(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}
