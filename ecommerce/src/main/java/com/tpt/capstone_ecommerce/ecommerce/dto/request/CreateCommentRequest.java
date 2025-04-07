package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequest {
    @NotBlank(message = "Comment user id cannot be blank")
    private String content;

    @NotBlank(message = "Comment user id cannot be blank")
    @Size(min = 36, max = 36, message = "Comment user id length is 36")
    private String userId;
}
