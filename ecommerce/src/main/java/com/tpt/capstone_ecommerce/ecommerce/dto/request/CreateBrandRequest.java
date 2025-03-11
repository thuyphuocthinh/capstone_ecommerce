package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequest {
    @NotBlank(message = "Brand name cannot be blank")
    @Size(min = 2, message = "Brand name min length is 2")
    private String name;

    @NotBlank(message = "Brand description cannot be blank")
    @Size(min = 8, message = "Brand description min length is 8")
    private String description;

    @NotNull(message = "Brand image cannot be null")
    private MultipartFile file;
}
