package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateLocationRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.LocationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> addLocationHandler(@Valid @RequestBody CreateLocationRequest createLocationRequest) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .message("Success")
                .data(this.locationService.createLocation(createLocationRequest))
                .build();

        return new ResponseEntity<>(
                apiSuccessResponse,
                HttpStatus.CREATED
        );
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLocationHandler(@PathVariable String id, @Valid @RequestBody UpdateLocationRequest updateLocationRequest) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.locationService.updateLocation(id, updateLocationRequest))
                .message("Success")
                .build();

        return new ResponseEntity<>(
                apiSuccessResponse,
                HttpStatus.OK
        );
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocationHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder().data(
                this.locationService.deleteLocation(id)
        ).message("Success").build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/provinces")
    public ResponseEntity<?> getAllProvincesHandler() {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder().data(
                this.locationService.getListProvinces()
        ).message("Success").build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/provinces/{id}/districts")
    public ResponseEntity<?> getAllDistrictsHandler(@PathVariable String id) {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder().data(
                this.locationService.getListDistricts(id)
        ).message("Success").build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/provinces/districts/{id}/wards")
    public ResponseEntity<?> getAllWardsHandler(@PathVariable String id) {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder().data(
                this.locationService.getListWards(id)
        ).message("Success").build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }
}
