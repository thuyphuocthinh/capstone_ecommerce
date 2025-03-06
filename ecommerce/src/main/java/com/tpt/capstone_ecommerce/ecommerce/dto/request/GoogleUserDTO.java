package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleUserDTO {
    private String sub;
    private String givenName;
    private String familyName;
    private String email;
}
