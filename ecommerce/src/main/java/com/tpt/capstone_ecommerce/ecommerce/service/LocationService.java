package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LocationDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface LocationService {
    String getFullLocation(String locationId);
    List<String> getLocationAllIds(String locationId);
    String createLocation(CreateLocationRequest request) throws BadRequestException;
    LocationDetailResponse updateLocation(String locationId, UpdateLocationRequest request) throws NotFoundException;
    String deleteLocation(String locationId) throws NotFoundException;
    List<LocationDetailResponse> getListProvinces();
    List<LocationDetailResponse> getListDistricts(String provinceId) throws NotFoundException;
    List<LocationDetailResponse> getListWards(String districtId) throws NotFoundException;
}
