package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuDetailResponse {
    private String id;
    private String name;
    private String imageUrl;
    private int quantity;
    private double price;
    private double discount;
    private String color;
    private String size;
    private String spuId;
}
