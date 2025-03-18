package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SkuDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SpuDetailForClientResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;

public interface SkuService {
    String addSku(String id, CreateSkuRequest createSkuRequest) throws NotFoundException, IOException;
    SkuDetailResponse getSkuDetail(String skuId) throws NotFoundException;
    SkuDetailResponse updateSku(String skuId, UpdateSkuRequest request) throws NotFoundException, IOException;
    String deleteSku(String skuId, boolean isHard) throws NotFoundException, BadRequestException;
    SkuDetailResponse changeStatus(String skuId, String status) throws NotFoundException, BadRequestException;
    SpuDetailForClientResponse getListSkusForClientBySpuId(String spuId);
    List<SkuDetailResponse> getListSkusDashboardBySpuId(String spuId);
}
