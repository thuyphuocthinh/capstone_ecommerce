package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.ShopService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<?> createShopHandler(@Valid @ModelAttribute CreateShopRequest request) throws IOException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.createShop(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> createShopHandler(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateShopRequest request
    ) throws IOException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.updateShop(id, request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShopByIdHandler(@PathVariable String id) throws IOException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.getShopById(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShopByIdHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.deleteShop(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getListShopsHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.shopService.getListShops(pageNumber, pageSize), HttpStatus.OK);
    }

    @PatchMapping("/{id}/change-status/{status}")
    public ResponseEntity<?> changeShopStatusHandler(
            @PathVariable String id,
            @PathVariable String status
    ) throws BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.changeShopStatus(id, status))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
