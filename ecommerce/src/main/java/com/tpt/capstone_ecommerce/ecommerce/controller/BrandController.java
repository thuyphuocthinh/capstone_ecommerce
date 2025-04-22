package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/brands")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<?> createBrandHandler(@Valid @ModelAttribute CreateBrandRequest request) throws IOException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.brandService.createBrand(request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBrandHandler(
            @PathVariable String id,
            @ModelAttribute UpdateBrandRequest request) throws IOException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.brandService.updateBrand(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandDetailHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.brandService.getBrandDetail(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrandHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.brandService.deleteBrand(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getListBrandsHandler() throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.brandService.getAllBrands())
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }
}
