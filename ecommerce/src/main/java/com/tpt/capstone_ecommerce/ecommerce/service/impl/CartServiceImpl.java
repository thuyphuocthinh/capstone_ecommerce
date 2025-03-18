package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.CartErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SkuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCartItemRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CartDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CartItemDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Cart;
import com.tpt.capstone_ecommerce.ecommerce.entity.CartItem;
import com.tpt.capstone_ecommerce.ecommerce.entity.Sku;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.CartItemRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.CartRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.SkuRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.CartService;
import org.apache.coyote.BadRequestException;
import org.checkerframework.checker.units.qual.N;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final SkuRepository skuRepository;

    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, SkuRepository skuRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.skuRepository = skuRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartDetailResponse getCartDetail(String userId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        Cart cart = this.cartRepository.findByUser(user).orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_NOT_FOUND));

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemDetailResponse> cartItemDetailResponses;

        cartItemDetailResponses = cartItems.stream().map(cartItem -> {
            return CartItemDetailResponse.builder()
                    .id(cartItem.getId())
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .shopId(cartItem.getSku().getSpu().getShop().getId())
                    .discount(cartItem.getDiscount())
                    .skuId(cartItem.getSku().getId())
                    .skuName(cartItem.getSku().getName())
                    .skuImageUrl(cartItem.getSku().getImageUrl())
                    .build();
        }).toList();

        return CartDetailResponse.builder()
                .totalPrice(cart.getTotalPrice())
                .totalQuantity(cart.getTotalQuantity())
                .cartItems(cartItemDetailResponses)
                .build();
    }

    @Override
    public String addCartItemToCart(String cartId, CreateCartItemRequest request) throws NotFoundException, BadRequestException {
        Cart findCart = this.cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_NOT_FOUND));

        String skuId = request.getSkuId();
        int quantity = request.getQuantity();

        if(quantity <= 0) {
            throw new BadRequestException(CartErrorConstant.QUANTITY_CANNOT_BE_LESS_THAN_OR_EQUAL_ZERO);
        }

        List<CartItem> cartItems = findCart.getCartItems();
        boolean found = false;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getSku().getId().equals(skuId)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            Sku findSku = this.skuRepository.findById(skuId).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));
            CartItem cartItem = new CartItem();
            cartItem.setCart(findCart);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(findSku.getPrice());
            cartItem.setDiscount(findSku.getDiscount());
            cartItem.setSku(findSku);
            cartItems.add(cartItem);
        }

        double totalPrice = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        findCart.setTotalPrice(totalPrice);
        findCart.setTotalQuantity(totalQuantity);
        this.cartRepository.save(findCart);
        return "Success";
    }

    @Override
    public String removeCartItem(String cartItemId) throws NotFoundException {
        CartItem findCartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_ITEM_NOT_FOUND));
        Cart findCart = this.cartRepository.findById(findCartItem.getCart().getId())
                .orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_NOT_FOUND));

        // Cập nhật totalQuantity & totalPrice trước khi xóa
        findCart.setTotalQuantity(findCart.getTotalQuantity() - findCartItem.getQuantity());
        findCart.setTotalPrice(findCart.getTotalPrice() - findCartItem.getUnitPrice() * findCartItem.getQuantity());

        // Xóa cartItem khỏi danh sách
        findCart.getCartItems().remove(findCartItem);

        // Xóa CartItem khỏi DB
        this.cartItemRepository.delete(findCartItem);

        // Lưu lại giỏ hàng
        this.cartRepository.save(findCart);

        return "Success";
    }

    @Override
    public String updateCartItem(String cartItemId, int quantity) throws NotFoundException, BadRequestException {
        CartItem findCartItem = this.cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_ITEM_NOT_FOUND));
        Cart findCart = this.cartRepository.findById(findCartItem.getCart().getId()).orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_NOT_FOUND));

        List<CartItem> cartItems = findCart.getCartItems();

        if(quantity != -1 && quantity != 1) {
            throw new BadRequestException(CartErrorConstant.INVALID_QUANTITY);
        }

        int newQuantity = findCartItem.getQuantity() + quantity;

        if(newQuantity <= 0) {
            this.removeCartItem(cartItemId);
        } else {
            double oldTotalPrice = findCart.getTotalPrice();
            findCartItem.setQuantity(newQuantity);
            findCart.setTotalQuantity(findCart.getTotalQuantity() + quantity);
            findCart.setTotalPrice(oldTotalPrice + quantity * findCartItem.getUnitPrice());
            this.cartRepository.save(findCart);
        }

        return "Success";
    }

    @Override
    public String clearCart(String cartId, List<String> listOfCartItemIds) throws NotFoundException {
        Cart findCart = this.cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException(CartErrorConstant.CART_NOT_FOUND));

        List<CartItem> cartItemsPurchased = this.cartItemRepository.findAllById(listOfCartItemIds);

        List<CartItem> cartItems = findCart.getCartItems();

        double totalPrice = cartItemsPurchased.stream().mapToDouble(CartItem::getTotalPrice).sum();
        int totalQuantity = cartItemsPurchased.stream().mapToInt(CartItem::getQuantity).sum();
        cartItems.removeIf(cartItem -> listOfCartItemIds.contains(cartItem.getId()));
        findCart.setTotalPrice(findCart.getTotalPrice() - totalPrice);
        findCart.setTotalQuantity(findCart.getTotalQuantity() -  totalQuantity);

        cartRepository.save(findCart);

        return "Cart items removed successfully";
    }

}
