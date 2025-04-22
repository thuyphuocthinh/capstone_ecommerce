package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Category;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.CacheCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class CacheCategoryImpl implements CacheCategory {
    private final JedisPool jedisPool;

    @Override
    public List<CategoryDetailResponse> getListCategories(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> categoriesMap = jedis.hgetAll(key);
            List<CategoryDetailResponse> categoryDetailResponses = new ArrayList<>();
            categoriesMap.forEach((k, v) -> {
                CategoryDetailResponse categoryDetail = new CategoryDetailResponse();

                try {
                    JSONObject json = new JSONObject(v);
                    categoryDetail.setId(k);
                    categoryDetail.setName(json.optString("name"));
                    categoryDetail.setDescription(json.optString("description"));
                    categoryDetail.setImageUrl(json.optString("imageUrl"));
                    categoryDetail.setSlug(json.optString("slug"));
                    categoryDetail.setParentId(json.optString("parentId"));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                categoryDetailResponses.add(categoryDetail);
            });
            return categoryDetailResponses;
        }
    }

    @Override
    public void saveListCategories(String key, List<CategoryDetailResponse> categoryDetailResponses) {
        try (Jedis jedis = jedisPool.getResource()) {
            for (CategoryDetailResponse category : categoryDetailResponses) {
                Map<String, String> categoryData = new HashMap<>();
                categoryData.put("name", category.getName());
                categoryData.put("description", category.getDescription());
                categoryData.put("imageUrl", category.getImageUrl());
                categoryData.put("slug", category.getSlug());
                categoryData.put("parentId", category.getParentId());

                String categoryJson = new JSONObject(categoryData).toString();

                jedis.hset(key, category.getId(), categoryJson);
            }
        }
    }

    @Override
    public void incrementTotalItems(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.incrBy(key, 1);
        }
    }

    @Override
    public void decrementTotalItems(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.decrBy(key, 1);
        }
    }

    @Override
    public String getTotalItems(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public void addNewCategory(String key, Category category) {
        try(Jedis jedis = jedisPool.getResource()) {
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("name", category.getName());
            categoryData.put("description", category.getDescription());
            categoryData.put("imageUrl", category.getImageUrl());
            categoryData.put("slug", category.getSlug());
            categoryData.put("parentId", category.getParentId());
            String categoryJson = new JSONObject(categoryData).toString();
            jedis.hset(key, category.getId(), categoryJson);
        }
    }

    @Override
    public void removeCategory(String key, String categoryId) {
        try (Jedis jedis = jedisPool.getResource()) {
            long result = jedis.hdel(key, categoryId);
            if (result == 1) {
                log.info("Category with ID '{}' has been removed from cache '{}'.", categoryId, key);
            } else {
                log.warn("Category with ID '{}' was not found in cache '{}'.", categoryId, key);
            }
        } catch (Exception e) {
            log.error("Failed to remove category with ID '{}' from cache '{}': {}", categoryId, key, e.getMessage());
        }
    }

    @Override
    public void updateCategory(String key, String categoryId, Category updatedCategory) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("name", updatedCategory.getName());
            categoryData.put("description", updatedCategory.getDescription());
            categoryData.put("imageUrl", updatedCategory.getImageUrl());
            categoryData.put("slug", updatedCategory.getSlug());
            categoryData.put("parentId", updatedCategory.getParentId());
            String categoryJson = new JSONObject(categoryData).toString();
            jedis.hset(key, categoryId, categoryJson);
            log.info("Category with ID '{}' has been updated in cache '{}'.", categoryId, key);
        } catch (Exception e) {
            log.error("Failed to update category with ID '{}' in cache '{}': {}", categoryId, key, e.getMessage());
        }
    }
}
