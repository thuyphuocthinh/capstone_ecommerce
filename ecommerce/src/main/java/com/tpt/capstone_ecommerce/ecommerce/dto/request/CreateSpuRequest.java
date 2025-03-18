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
public class CreateSpuRequest {
    @NotBlank(message = "SPU name cannot be blank")
    @Size(min = 2, message = "SPU name min length is 2")
    private String name;

    @NotBlank(message = "SPU description cannot be blank")
    @Size(min = 8, message = "SPU description min length is 8")
    private String description;

    @NotBlank(message = "SPU brand id cannot be blank")
    @Size(min = 36, max = 36, message = "SPU brand id length is 36")
    private String brandId;

    @NotBlank(message = "SPU category id cannot be blank")
    @Size(min = 36, max = 36, message = "SPU category id length is 36")
    private String categoryId;

    @NotBlank(message = "SPU shop id cannot be blank")
    @Size(min = 36, max = 36, message = "SPU shop id length is 36")
    private String shopId;

    @NotNull(message = "SPU image cannot be null")
    private MultipartFile file;
}
