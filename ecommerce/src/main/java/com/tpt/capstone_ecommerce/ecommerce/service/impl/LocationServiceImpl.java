package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.entity.Location;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.LocationRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.LocationService;
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
}
