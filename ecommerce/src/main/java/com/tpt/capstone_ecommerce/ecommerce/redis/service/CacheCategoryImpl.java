package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;
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
                    JSONObject json = new JSONObject(Integer.parseInt(v));
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
}
