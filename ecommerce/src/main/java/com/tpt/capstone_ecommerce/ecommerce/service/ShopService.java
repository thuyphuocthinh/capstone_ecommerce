package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.ShopDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.io.IOException;

public interface ShopService {
    String createShop(CreateShopRequest createShopRequest) throws IOException;
    ShopDetailResponse updateShop(String shopId, UpdateShopRequest updateShopRequest) throws IOException, NotFoundException;
    ShopDetailResponse getShopById(String shopId) throws NotFoundException, BadRequestException;
    String deleteShop(String shopId) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getListShops(Integer pageNumber, Integer pageSize);
    String changeShopStatus(String shopId, String status) throws NotFoundException, BadRequestException;
}
