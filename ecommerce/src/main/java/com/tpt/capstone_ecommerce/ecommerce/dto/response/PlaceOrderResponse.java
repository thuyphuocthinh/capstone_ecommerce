package com.tpt.capstone_ecommerce.ecommerce.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceOrderResponse {
    private String orderId;
    private String orderStatus;
    private String paymentRedirectUrl;
    private boolean isPaidByCash;
}
