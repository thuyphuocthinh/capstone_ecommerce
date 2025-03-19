package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCartItemRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{id}/cart-items")
    public ResponseEntity<?> addCartItemHandler(@PathVariable String id, @Valid @RequestBody CreateCartItemRequest createCartItemRequest) throws BadRequestException {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.cartService.addCartItemToCart(id, createCartItemRequest))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("@customSecurityExpression.isOwner(#id, authentication)")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getCartDetailHandler(@PathVariable String id) {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.cartService.getCartDetail(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/cart-items/{id}/quantity/{quantity}")
    public ResponseEntity<?> updateCartItemQuantityHandler(@PathVariable String id, @PathVariable Integer quantity) throws BadRequestException {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.cartService.updateCartItem(id, quantity))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<?> deleteCartItemHandler(@PathVariable String id) throws BadRequestException {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .message("Success")
                .data(this.cartService.removeCartItem(id))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
