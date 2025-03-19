package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCartItemRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CartDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CartItemDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface CartService {
    CartDetailResponse getCartDetail(String userId) throws NotFoundException;
    String addCartItemToCart(String cartId, CreateCartItemRequest request) throws NotFoundException, BadRequestException;
    String removeCartItem(String cartItemId) throws NotFoundException, BadRequestException;
    String updateCartItem(String cartItemId, int quantity) throws NotFoundException, BadRequestException;
    String clearCart(String cartId, List<String> listOfCartItemIds) throws NotFoundException, BadRequestException;
}
