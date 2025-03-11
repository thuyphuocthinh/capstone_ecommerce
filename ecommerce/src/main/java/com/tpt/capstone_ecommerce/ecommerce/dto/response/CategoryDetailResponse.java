package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDetailResponse {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String slug;
    private String parentId;
}
