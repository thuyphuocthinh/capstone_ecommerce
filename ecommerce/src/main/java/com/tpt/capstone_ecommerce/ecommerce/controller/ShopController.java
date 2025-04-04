package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.DiscountService;
import com.tpt.capstone_ecommerce.ecommerce.service.ShopService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/shops")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class ShopController {

    private final ShopService shopService;

    private final DiscountService discountService;

    public ShopController(ShopService shopService, DiscountService discountService) {
        this.shopService = shopService;
        this.discountService = discountService;
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
    public ResponseEntity<?> updateShopHandler(
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

    @PostMapping("/{id}/discounts")
    public ResponseEntity<?> createDiscountByShopHandler(
            @PathVariable String id,
            @Valid @RequestBody CreateDiscountRequest request
    ) throws BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.createDiscount(id, request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/discounts")
    public ResponseEntity<?> getDiscountByShops(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws BadRequestException {
        return new ResponseEntity<>(
                this.discountService.getDiscountsByCreator(id, pageNumber, pageSize),
                HttpStatus.OK
        );
    }

    @GetMapping("/discounts/{id}")
    public ResponseEntity<?> getDiscountDetailByShopHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.getDiscountDetail(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/discounts/{id}")
    public ResponseEntity<?> updateDiscountByShopHandler(@PathVariable String id, UpdateDiscountRequest request) throws BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.updateDiscount(id, request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<?> deleteDiscountByShopHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.deleteDiscount(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/discounts/{id}/change-status/{status}")
    public ResponseEntity<?> changeStatusDiscountByShopHandler(@PathVariable String id, @PathVariable DISCOUNT_STATUS status) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.changeDiscountStatus(id, status.name()))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
