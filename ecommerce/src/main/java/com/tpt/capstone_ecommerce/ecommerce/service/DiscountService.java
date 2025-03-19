package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.DiscountDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Discount;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface DiscountService {
    String createDiscount(String creatorId, CreateDiscountRequest request) throws RuntimeException, BadRequestException;
    DiscountDetailResponse getDiscountDetail(String discountId) throws NotFoundException, BadRequestException;
    DiscountDetailResponse updateDiscount(String discountId, UpdateDiscountRequest request) throws NotFoundException, BadRequestException;
    String deleteDiscount(String discountId) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getDiscountsByCreator(String creatorId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getAllDiscounts(Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    String changeDiscountStatus(String discountId, String status) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getDiscountsByAmountAndShop(String shopId, Double amount, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getGlobalDiscountsByAmount(Double amount, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
}
