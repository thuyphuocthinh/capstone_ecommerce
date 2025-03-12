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
    String deleteSpu(String id, boolean isHard) throws NotFoundException, BadRequestException;
    SpuDetailResponse changeSpuStatus(String id, String status) throws NotFoundException, BadRequestException;
}
