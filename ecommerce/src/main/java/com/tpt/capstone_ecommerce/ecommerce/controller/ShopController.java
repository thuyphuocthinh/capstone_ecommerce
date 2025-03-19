package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.aop.annotation.ValidEnum;
import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SkuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SpuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_ITEM_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SPU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.*;
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

    private final OrderService orderService;

    private final SpuService spuService;

    private final SkuService skuService;

    public ShopController(ShopService shopService, DiscountService discountService, OrderService orderService, SpuService spuService, SkuService skuService) {
        this.shopService = shopService;
        this.discountService = discountService;
        this.orderService = orderService;
        this.spuService = spuService;
        this.skuService = skuService;
    }

    @PostMapping("/spus")
    public ResponseEntity<?> createSpuHandler(@Valid @ModelAttribute CreateSpuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.createSpu(request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
    @GetMapping("/{id}/spus")
    public ResponseEntity<?> getSpuByShopHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws BadRequestException {
        return new ResponseEntity<>(this.spuService.getListsSpuByShop(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PatchMapping("/spus/skus/{id}")
    public ResponseEntity<?> updateSkuHandler(@PathVariable String id, @ModelAttribute UpdateSkuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.updateSku(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/spus/skus/{id}/hard/{isHard}")
    public ResponseEntity<?> deleteSkuHandler(@PathVariable String id, @PathVariable Boolean isHard) throws BadRequestException {
        if(isHard == null){
            throw new BadRequestException(SpuErrorConstant.LACK_IS_HARD);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.deleteSku(id, isHard))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/skus/{id}/change-status/{status}")
    public ResponseEntity<?> updateSkuStatusHandler(@PathVariable String id, @PathVariable String status) throws IOException {
        if(!status.equals(SKU_STATUS.ACTIVE.name()) && !status.equals(SKU_STATUS.INACTIVE.name())) {
            throw new BadRequestException(SkuErrorConstant.INVALID_STATUS);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.changeStatus(id, status))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/{id}")
    public ResponseEntity<?> updateSpuHandler(@PathVariable String id, @ModelAttribute UpdateSpuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.updateSpu(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/{id}/change-status/{status}")
    public ResponseEntity<?> updateSpuStatusHandler(@PathVariable String id, @PathVariable String status) throws IOException {
        if(!status.equals(SPU_STATUS.ACTIVE.name()) && !status.equals(SPU_STATUS.INACTIVE.name())) {
            throw new BadRequestException(SpuErrorConstant.INVALID_STATUS);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.changeSpuStatus(id, status))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/spus/{id}/hard/{isHard}")
    public ResponseEntity<?> deleteSpuHandler(@PathVariable String id, @PathVariable Boolean isHard) throws BadRequestException {
        if(isHard == null){
            throw new BadRequestException(SpuErrorConstant.LACK_IS_HARD);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.deleteSpu(id, isHard))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/spus/{id}/skus")
    public ResponseEntity<?> createSkuHandler(@Valid @ModelAttribute CreateSkuRequest request, @PathVariable String id) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.addSku(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> createShopHandler(@Valid @ModelAttribute CreateShopRequest request) throws IOException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.createShop(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
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

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShopByIdHandler(@PathVariable String id) throws IOException {
        APISuccessResponse<?> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.shopService.getShopById(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
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

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
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

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
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
    public ResponseEntity<?> getDiscountDetailByShopHandler(@PathVariable String id) throws NotFoundException, BadRequestException {
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
    public ResponseEntity<?> deleteDiscountByShopHandler(@PathVariable String id) throws NotFoundException, BadRequestException {
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

    @PreAuthorize("@customSecurityExpression.isShopOwner(#id, authentication)")
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getOrdersByShops(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws BadRequestException {
        return new ResponseEntity<>(
                this.orderService.getListOrderByShop(id, pageNumber, pageSize),
                HttpStatus.OK
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetailByShops(
            @PathVariable String id
    ) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.orderService.getOrderItemDetailByShop(id))
                .build();

        return new ResponseEntity<>(
                apiSuccessResponse,
                HttpStatus.OK
        );
    }

    @PatchMapping("/orders/{id}/change-status/{status}")
    public ResponseEntity<?> getOrderDetailByShops(
            @PathVariable String id,
            @PathVariable @ValidEnum(enumClass = ORDER_ITEM_STATUS.class) String status
    ) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.orderService.updateOrderItemStatusByShop(id, status))
                .build();

        return new ResponseEntity<>(
                apiSuccessResponse,
                HttpStatus.OK
        );
    }
}
