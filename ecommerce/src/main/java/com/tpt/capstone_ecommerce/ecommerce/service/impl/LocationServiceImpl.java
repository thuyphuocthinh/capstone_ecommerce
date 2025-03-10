package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.LocationErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LocationDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Location;
import com.tpt.capstone_ecommerce.ecommerce.enums.LOCATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.LocationRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.LocationService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public String getFullLocation(String locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("Location not found"));
        if(location.getParentId() == null) {
            return location.getName();
        }
        return location.getName() + ", " + getFullLocation(location.getParentId());
    }

    @Override
    public List<String> getLocationAllIds(String locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("Location not found"));
        List<String> list = new ArrayList<>();
        list.add(location.getId());
        while(location.getParentId() != null) {
            location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("Location not found"));
            list.add(location.getId());
        }
        return list;
    }

    @Override
    public String createLocation(CreateLocationRequest request) throws BadRequestException {
        LOCATION_TYPE locationType = request.getLocationType();
        String locationName = request.getName().trim().toUpperCase();
        String locationParentId = request.getParentId();
        if(locationType == LOCATION_TYPE.PROVINCE) {
            Location location = this.locationRepository.findByName(locationName);
            if(location != null) {
                throw new BadRequestException(LocationErrorConstant.LOCATION_ALREADY_EXISTS);
            }
        }

        Location location = Location.builder()
                .type(locationType)
                .name(locationName)
                .parentId(locationParentId)
                .build();

        Location savedLocation = this.locationRepository.save(location);

        return savedLocation.getId();
    }

    @Override
    public LocationDetailResponse updateLocation(String locationId, UpdateLocationRequest request) throws NotFoundException {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND));

        String name = request.getName();
        String parentId = request.getParentId();
        LOCATION_TYPE type = request.getType();

        if(name != null) {
            location.setName(name.trim().toUpperCase());
        }

        if(parentId != null) {
            location.setParentId(parentId);
        }

        if(type != null) {
            location.setType(type);
        }

        Location saved = this.locationRepository.save(location);

        return LocationDetailResponse.builder()
                .id(saved.getId())
                .parentId(saved.getParentId())
                .name(saved.getName().toUpperCase())
                .build();
    }

    @Override
    public String deleteLocation(String locationId) throws NotFoundException {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND));

        LOCATION_TYPE type = location.getType();
        if(type == LOCATION_TYPE.PROVINCE || type == LOCATION_TYPE.DISTRICT) {
            this.locationRepository.deleteProvinceOrDistrict(locationId);
        } else {
            this.locationRepository.delete(location);
        }

        return "Success";
    }

    @Override
    public List<LocationDetailResponse> getListProvinces() {
        List<Location> list = this.locationRepository.findAllByType(LOCATION_TYPE.PROVINCE);

        return list.stream().map(location -> LocationDetailResponse.builder()
                .type(location.getType().name())
                .name(location.getName())
                .id(location.getId())
                .parentId(null)
                .build()).toList();
    }

    @Override
    public List<LocationDetailResponse> getListDistricts(String provinceId) throws NotFoundException {
        Location check = this.locationRepository.findById(provinceId).orElseThrow(() -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND));

        List<Location> list = this.locationRepository.findAllByParentId(provinceId);

        return list.stream().map(location -> LocationDetailResponse.builder()
                .type(location.getType().name())
                .name(location.getName())
                .id(location.getId())
                .parentId(null)
                .build()).toList();
    }

    @Override
    public List<LocationDetailResponse> getListWards(String districtId) throws NotFoundException {
        Location check = this.locationRepository.findById(districtId).orElseThrow(() -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND));
        List<Location> list = this.locationRepository.findAllByParentId(districtId);

        return list.stream().map(location -> LocationDetailResponse.builder()
                .type(location.getType().name())
                .name(location.getName())
                .id(location.getId())
                .parentId(null)
                .build()).toList();
    }
}
