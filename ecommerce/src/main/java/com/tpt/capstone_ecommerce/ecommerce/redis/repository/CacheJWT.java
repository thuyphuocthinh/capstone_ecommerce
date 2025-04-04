package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

public interface CacheJWT {
    void setAccessToken(String key, String accessToken, int expiresIn);
    String getAccessToken(String key);
    void deleteAccessToken(String key);
}


/*
* Cache JWT is beneficial for cases in which multiple devices login using the same account
* And then password of this account is changed in a device => other devices must log in again to authorize
* */