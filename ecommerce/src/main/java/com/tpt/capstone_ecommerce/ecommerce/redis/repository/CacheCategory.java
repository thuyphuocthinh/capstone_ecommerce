package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Category;

import java.util.List;

public interface CacheCategory {
    List<CategoryDetailResponse> getListCategories(String key);
    void saveListCategories(String key, List<CategoryDetailResponse> categoryDetailResponses);
    void incrementTotalItems(String key);
    void decrementTotalItems(String key);
    String getTotalItems(String key);
    void addNewCategory(String key, Category category);
    void removeCategory(String key, String categoryId);
    void updateCategory(String key, String categoryId, Category updatedCategory);
}
