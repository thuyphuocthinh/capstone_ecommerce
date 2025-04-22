package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.BrandDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;

import java.util.List;

public interface CacheBrand {
    List<BrandDetailResponse> getListCategories(String key);
    void saveListBrands(String key, List<BrandDetailResponse> brandDetailResponses);
    void addNewBrand(String key, Brand brand);
    void removeBrand(String key, String brandId);
    void updateBrand(String key, String brandId, Brand updatedBrand);
}
