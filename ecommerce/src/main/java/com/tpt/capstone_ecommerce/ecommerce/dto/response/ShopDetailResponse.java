package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDetailResponse {
    private String id;
    private String name;
    private String description;
    private String phone;
    private String address;
    private String ownerName;
    private String ownerId;
    private String imageUrl;
}
