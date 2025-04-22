package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.BrandDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.CacheBrand;
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
@Service
@Slf4j
public class CacheBrandImpl implements CacheBrand {
    private final JedisPool jedisPool;

    @Override
    public List<BrandDetailResponse> getListCategories(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> brandsMap = jedis.hgetAll(key);
            List<BrandDetailResponse> brandDetailResponses = new ArrayList<>();
            brandsMap.forEach((k, v) -> {
                BrandDetailResponse brandDetail = new BrandDetailResponse();

                try {
                    JSONObject json = new JSONObject(v);
                    brandDetail.setId(k);
                    brandDetail.setName(json.optString("name"));
                    brandDetail.setDescription(json.optString("description"));
                    brandDetail.setImageUrl(json.optString("imageUrl"));
                    brandDetail.setSlug(json.optString("slug"));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                brandDetailResponses.add(brandDetail);
            });
            return brandDetailResponses;
        } catch (Exception e) {
            log.error("Error get list of brands from cache: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void saveListBrands(String key, List<BrandDetailResponse> brandDetailResponses) {
        try (Jedis jedis = jedisPool.getResource()) {
            for (BrandDetailResponse brand : brandDetailResponses) {
                Map<String, String> categoryData = new HashMap<>();
                categoryData.put("name", brand.getName());
                categoryData.put("description", brand.getDescription());
                categoryData.put("imageUrl", brand.getImageUrl());
                categoryData.put("slug", brand.getSlug());

                String brandJson = new JSONObject(categoryData).toString();
                jedis.hset(key, brand.getId(), brandJson);
            }
        }
    }

    @Override
    public void addNewBrand(String key, Brand brand) {
        try(Jedis jedis = jedisPool.getResource()) {
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("name", brand.getName());
            categoryData.put("description", brand.getDescription());
            categoryData.put("imageUrl", brand.getImageUrl());
            categoryData.put("slug", brand.getSlug());
            String categoryJson = new JSONObject(categoryData).toString();
            jedis.hset(key, brand.getId(), categoryJson);
        }
    }

    @Override
    public void removeBrand(String key, String brandId) {
        try (Jedis jedis = jedisPool.getResource()) {
            long result = jedis.hdel(key, brandId);
            if (result == 1) {
                log.info("Category with ID '{}' has been removed from cache '{}'.", brandId, key);
            } else {
                log.warn("Category with ID '{}' was not found in cache '{}'.", brandId, key);
            }
        } catch (Exception e) {
            log.error("Failed to remove category with ID '{}' from cache '{}': {}", brandId, key, e.getMessage());
        }
    }

    @Override
    public void updateBrand(String key, String brandId, Brand updatedBrand) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("name", updatedBrand.getName());
            categoryData.put("description", updatedBrand.getDescription());
            categoryData.put("imageUrl", updatedBrand.getImageUrl());
            categoryData.put("slug", updatedBrand.getSlug());
            String categoryJson = new JSONObject(categoryData).toString();
            jedis.hset(key, brandId, categoryJson);
            log.info("Category with ID '{}' has been updated in cache '{}'.", brandId, key);
        } catch (Exception e) {
            log.error("Failed to update category with ID '{}' in cache '{}': {}", brandId, key, e.getMessage());
        }
    }
}
