package com.tpt.capstone_ecommerce.ecommerce.dto.request;

import com.tpt.capstone_ecommerce.ecommerce.enums.LOCATION_TYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLocationRequest {
    private String name;
    private String parentId;
    private LOCATION_TYPE type;
}
