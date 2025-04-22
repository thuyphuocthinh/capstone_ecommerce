package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.BrandDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;

public interface BrandService {
    String createBrand(CreateBrandRequest createBrandRequest) throws IOException;
    BrandDetailResponse getBrandDetail(String id) throws NotFoundException;
    BrandDetailResponse updateBrand(String id, UpdateBrandRequest updateBrandRequest) throws IOException;
    String deleteBrand(String id) throws NotFoundException;
    List<BrandDetailResponse> getAllBrands() throws NotFoundException;
}
