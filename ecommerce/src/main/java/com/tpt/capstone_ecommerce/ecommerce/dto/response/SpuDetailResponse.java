package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpuDetailResponse {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String slug;
    private String brandId;
    private String brandName;
    private String categoryId;
    private String categoryName;
    private String shopId;
    private double price;
    private double discount;
}
