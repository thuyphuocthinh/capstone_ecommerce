package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDetailResponse {
    private String id;
    private String skuId;
    private String skuName;
    private String skuImageUrl;
    private int quantity;
    private double unitPrice;
    private double discount;
}
