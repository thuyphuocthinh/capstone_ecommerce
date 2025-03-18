package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SpuDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Spu;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.io.IOException;

public interface SpuService {
    String createSpu(CreateSpuRequest createSpuRequest) throws NotFoundException, IOException;
    SpuDetailResponse getSpuDetail(String id) throws NotFoundException, BadRequestException;
    SpuDetailResponse updateSpu(String id, UpdateSpuRequest request) throws NotFoundException, IOException;
    APISuccessResponseWithMetadata<?> getListsSpuHomepage(Integer pageNumber, Integer pageSize);
    APISuccessResponseWithMetadata<?> getListsSpuDashboard(Integer pageNumber, Integer pageSize);
    APISuccessResponseWithMetadata<?> getListsSpuByShop(String shopId, Integer pageNumber, Integer pageSize);
    APISuccessResponseWithMetadata<?> getListsSpuByBrand(String brandId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getListsSpuByCategory(String categoryId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    String deleteSpu(String id, boolean isHard) throws NotFoundException, BadRequestException;
    SpuDetailResponse changeSpuStatus(String id, String status) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> searchSpuByName(String name, String brandIds, String categoryIds, String sortBy, String sortDirection, Integer pageNumber, Integer pageSize) throws BadRequestException;
}
