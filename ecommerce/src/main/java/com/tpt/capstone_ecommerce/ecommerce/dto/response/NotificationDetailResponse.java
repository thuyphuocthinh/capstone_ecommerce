package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetailResponse {
    private String id;
    private String type;
    private String message;
    private String userId;
    private String referenceId;
}
