package com.tpt.capstone_ecommerce.ecommerce.service;

import java.util.List;

public interface LocationService {
    String getFullLocation(String locationId);
    List<String> getLocationAllIds(String locationId);
}
