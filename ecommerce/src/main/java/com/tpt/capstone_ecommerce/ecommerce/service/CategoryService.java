package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryNestedResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    String createCategory(CreateCategoryRequest request) throws IOException;
    CategoryDetailResponse getCategoryDetail(String id) throws NotFoundException;
    CategoryDetailResponse updateCategory(String id, UpdateCategoryRequest request) throws NotFoundException, IOException;
    String deleteCategory(String id) throws NotFoundException;
    List<CategoryDetailResponse> getAllCategories() throws NotFoundException;
    APISuccessResponseWithMetadata<?>  getCategoriesByParentId(String parentId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getParentCategories(Integer pageNumber, Integer pageSize) throws NotFoundException;
    List<CategoryNestedResponse> getNestedCategories() throws NotFoundException;
}
