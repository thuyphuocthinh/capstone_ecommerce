package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import com.tpt.capstone_ecommerce.ecommerce.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRetryPaymentResponse {
    private String orderId;
    private String paymentId;
    private String orderStatus;
    private double totalPrice;
    private double finalPrice;
    private int totalQuantity;
    List<OrderItemResponse> orderItemResponses;
}
