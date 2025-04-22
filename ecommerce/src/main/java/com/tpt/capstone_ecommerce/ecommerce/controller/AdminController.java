package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.DiscountService;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import com.tpt.capstone_ecommerce.ecommerce.service.ShopService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final DiscountService discountService;

    private final OrderService orderService;

    private final ShopService shopService;

    public AdminController(DiscountService discountService, OrderService orderService, ShopService shopService) {
        this.discountService = discountService;
        this.orderService = orderService;
        this.shopService = shopService;
    }

    @PatchMapping("/shops/{id}/change-status/{status}")
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
    public ResponseEntity<?> createDiscountByAdminHandler(
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
    public ResponseEntity<?> getDiscountByAdmins(
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
    public ResponseEntity<?> getDiscountDetailByAdminHandler(@PathVariable String id) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.getDiscountDetail(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/discounts/{id}")
    public ResponseEntity<?> updateDiscountByAdminHandler(@PathVariable String id, UpdateDiscountRequest request) throws BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.updateDiscount(id, request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<?> deleteDiscountByAdminHandler(@PathVariable String id) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.deleteDiscount(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/discounts/{id}/change-status/{status}")
    public ResponseEntity<?> changeStatusDiscountByAdminHandler(@PathVariable String id, @PathVariable DISCOUNT_STATUS status) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.discountService.changeDiscountStatus(id, status.name()))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getOrdersByAdminHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws BadRequestException {
        return new ResponseEntity<>(
                this.orderService.getListOrderByAdmin(pageNumber, pageSize),
                HttpStatus.OK
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrdersByAdminHandler(
            @PathVariable String id
    ) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.orderService.getOrderDetail(id))
                .build();
        
        return new ResponseEntity<>(
                apiSuccessResponse,
                HttpStatus.OK
        );
    }
}
