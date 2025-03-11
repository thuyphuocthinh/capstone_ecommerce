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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, message = "Category name min length is 2")
    private String name;

    @NotBlank(message = "Category description cannot be blank")
    @Size(min = 8, message = "Category description min length is 8")
    private String description;

    private String parentId;

    @NotNull(message = "Category image cannot be null")
    private MultipartFile file;
}
