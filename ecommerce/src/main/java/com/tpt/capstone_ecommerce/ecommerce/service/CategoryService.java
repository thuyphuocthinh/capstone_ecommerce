package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CategoryDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;

import java.io.IOException;

public interface CategoryService {
    String createCategory(CreateCategoryRequest request) throws IOException;
    CategoryDetailResponse getCategoryDetail(String id) throws NotFoundException;
    CategoryDetailResponse updateCategory(String id, UpdateCategoryRequest request) throws NotFoundException, IOException;
    String deleteCategory(String id) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getAllCategories(Integer pageNumber, Integer pageSize) throws NotFoundException;
}
