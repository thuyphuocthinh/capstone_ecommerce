package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.service.DiscountService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {
    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllDiscountsHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws BadRequestException {
        return new ResponseEntity<>(this.discountService.getAllDiscounts(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/get-by-shop")
    public ResponseEntity<?> getAllDiscountsByShopAndAmountHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "shopId", required = true) String shopId,
            @RequestParam(name = "amount", required = true) double amount
    ) throws BadRequestException {
        return new ResponseEntity<>(this.discountService.getDiscountsByAmountAndShop(shopId, amount, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/get-by-system")
    public ResponseEntity<?> getAllGlobalDiscountsAndAmountHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "amount", required = true) double amount
            ) throws BadRequestException {
        return new ResponseEntity<>(this.discountService.getGlobalDiscountsByAmount(amount, pageNumber, pageSize), HttpStatus.OK);
    }
}
