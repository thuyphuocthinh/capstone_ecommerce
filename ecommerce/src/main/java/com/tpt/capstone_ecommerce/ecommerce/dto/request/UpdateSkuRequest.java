package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSkuRequest {
    private String name;

    private String size;

    private String color;

    private String description;

    private double price;

    private double discount;

    private int quantity;

    private MultipartFile file;
}
