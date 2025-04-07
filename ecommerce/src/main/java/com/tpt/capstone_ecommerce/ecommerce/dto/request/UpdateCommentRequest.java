package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentRequest {
    @NotBlank(message = "Comment user id cannot be blank")
    private String content;
}
