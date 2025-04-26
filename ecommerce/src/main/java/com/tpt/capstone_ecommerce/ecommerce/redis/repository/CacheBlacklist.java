package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

public interface CacheBlacklist {
    boolean findAccessToken(String accessToken);
    void addNewAccessToken(String accessToken);
    void clear();
}
