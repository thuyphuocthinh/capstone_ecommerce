package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRedisDTO {
    private String orderId;
    private String paymentMethod;
    private double totalPrice;
    private String userEmail;
    private String cartId;
    private String userId;
}
