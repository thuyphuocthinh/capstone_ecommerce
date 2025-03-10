package com.tpt.capstone_ecommerce.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDetailResponse {
    private String id;
    private String specificAddress;
    private String fullName;
    private String phone;
    private String locationProvinceId;
    private String locationDistrictId;
    private String locationWardId;
}
