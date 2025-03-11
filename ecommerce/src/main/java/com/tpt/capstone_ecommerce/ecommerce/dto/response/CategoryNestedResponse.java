package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNestedResponse {
    private String id;
    private String name;
    private String slug;
    private List<CategoryDetailResponse> subCategories;
}
