package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface OrderService {
    CheckoutOrderResponse checkoutOrder(CheckoutOrderRequest checkoutOrderRequest);
    PlaceOrderResponse placeOrder(String email, PlaceOrderRequest placeOrderRequest, String ipAddress) throws Exception;
    OrderDetailResponse getOrderDetail(String orderId) throws NotFoundException;
    String cancelOrder(String orderId) throws NotFoundException, BadRequestException;
    OrderItemResponse getOrderItemDetailByShop (String orderItemId) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getListOrderByShop(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getListOrderByAdmin(Integer pageNumber, Integer pageSize) throws NotFoundException;
    String updateOrderItemStatusByShop(String orderItemId, String statusChange) throws NotFoundException, BadRequestException;
    String updateOrderStatus(String orderId) throws NotFoundException;
}
