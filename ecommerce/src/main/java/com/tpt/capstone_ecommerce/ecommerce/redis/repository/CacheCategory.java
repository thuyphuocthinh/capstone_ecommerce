package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;

import java.util.List;

public interface CacheCategory {
    List<CategoryDetailResponse> getListCategories(String key);
    void saveListCategories(String key, List<CategoryDetailResponse> categoryDetailResponses);
    void incrementTotalItems(String key);
    void decrementTotalItems(String key);
    String getTotalItems(String key);
}
