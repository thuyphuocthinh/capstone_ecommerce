package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CheckoutOrderResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PlaceOrderResponse;

public interface OrderService {
    CheckoutOrderResponse checkoutOrder(CheckoutOrderRequest checkoutOrderRequest);
    PlaceOrderResponse placeOrder(String userEmail, PlaceOrderRequest placeOrderRequest);
}
