package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface OrderService {
    CheckoutOrderResponse checkoutOrder(CheckoutOrderRequest checkoutOrderRequest) throws BadRequestException;
    PlaceOrderResponse placeOrder(String email, PlaceOrderRequest placeOrderRequest, String ipAddress) throws Exception;
    OrderDetailResponse getOrderDetail(String orderId) throws NotFoundException, BadRequestException;
    String cancelOrder(String orderId) throws NotFoundException, BadRequestException;
    OrderItemResponse getOrderItemDetailByShop (String orderItemId) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getListOrderByShop(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getListOrderByAdmin(Integer pageNumber, Integer pageSize) throws NotFoundException;
    String updateOrderItemStatusByShop(String orderItemId, String statusChange) throws NotFoundException, BadRequestException;
    void updateOrderStatus(String orderId) throws NotFoundException;
    OrderRetryPaymentResponse getOrderRetryPayment(String orderId) throws NotFoundException, BadRequestException;
}
