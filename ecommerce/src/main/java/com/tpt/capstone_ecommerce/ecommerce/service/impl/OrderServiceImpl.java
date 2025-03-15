package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.OrderDiscount;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CartItemDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CheckoutOrderResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PlaceOrderResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.*;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final AddressRepository addressRepository;

    private final DiscountRepository discountRepository;

    private final UserRepository userRepository;

    public OrderServiceImpl(CartItemRepository cartItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, AddressRepository addressRepository, DiscountRepository discountRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.discountRepository = discountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CheckoutOrderResponse checkoutOrder(CheckoutOrderRequest checkoutOrderRequest) {
        // step1: validate each cart items and current quantity of each
        List<String> cartItemIds = checkoutOrderRequest.getCartItemIds();
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        if (cartItems.size() != cartItemIds.size()) {
            throw new NotFoundException(CartErrorConstant.CART_ITEM_NOT_FOUND);
        }

        List<CartItemDetailResponse> cartItemDetailResponses = new ArrayList<>();

        int totalQuantity = cartItems.stream()
                .mapToInt(cartItem -> {
                    Sku sku = cartItem.getSku();
                    if (sku.getQuantity() == 0 || sku.getQuantity() < cartItem.getQuantity()) {
                        try {
                            throw new BadRequestException(SkuErrorConstant.SKU_OUT_OF_STOCK);
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    cartItemDetailResponses.add(
                            CartItemDetailResponse.builder()
                                    .id(cartItem.getId())
                                    .quantity(cartItem.getQuantity())
                                    .unitPrice(sku.getPrice())
                                    .discount(sku.getDiscount())
                                    .skuImageUrl(sku.getImageUrl())
                                    .skuName(sku.getName())
                                    .shopId(sku.getSpu().getShop().getId())
                                    .build()
                    );
                    return cartItem.getQuantity();
                }).sum();

        double totalPrice = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        // step2: create order response
        // step3: return to user

        return CheckoutOrderResponse.builder()
                .skuOrderItems(cartItemDetailResponses)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();
    }

    @Override
    public PlaceOrderResponse placeOrder(String userEmail, PlaceOrderRequest placeOrderRequest) {
        // addressId, orderItemIds
        // globalDiscounts, shopDiscounts
        String addressId = placeOrderRequest.getAddressId();
        List<String> orderItemIds = placeOrderRequest.getOrderItemIds();
        List<OrderDiscount> shopDiscounts = placeOrderRequest.getShopDiscounts();
        OrderDiscount globalDiscounts = placeOrderRequest.getGlobalDiscounts();
        String paymentMethod = placeOrderRequest.getPaymentMethod();

        // validate user
        User findUser = this.userRepository.findByEmail(userEmail).orElseThrow(
                () -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND)
        );

        // validate address
        Address findAddress = this.addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(AddressErrorConstant.ADDRESS_NOT_FOUND));

        // validate and create order items
        List<CartItem> cartItems = cartItemRepository.findAllById(orderItemIds);
        if (cartItems.size() != orderItemIds.size()) {
            throw new NotFoundException(CartErrorConstant.CART_ITEM_NOT_FOUND);
        }

        List<OrderItem> orderItemsList = new ArrayList<OrderItem>();

        int totalQuantity = cartItems.stream()
                .mapToInt(cartItem -> {
            Sku sku = cartItem.getSku();
            if (sku.getQuantity() == 0 || sku.getQuantity() < cartItem.getQuantity()) {
                try {
                    throw new BadRequestException(SkuErrorConstant.SKU_OUT_OF_STOCK);
                } catch (BadRequestException e) {
                    throw new RuntimeException(e);
                }
            }
            orderItemsList.add(
                    OrderItem.builder()
                            .quantity(cartItem.getQuantity())
                            .sku(cartItem.getSku())
                            .price(cartItem.getUnitPrice())
                            .discount(sku.getDiscount())
                            .build()
            );
            return cartItem.getQuantity();
        }).sum();

        // apply discount
        double totalPrice = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        double finalPrice = cartItems.stream()
                .mapToDouble(c -> c.getTotalPrice() - c.getTotalPrice() * c.getDiscount() / 100)
                .sum();

        Discount findGlobalDiscount = this.discountRepository.findByCode(
                globalDiscounts.getCode()
        );
        List<Discount> shopDiscountLists = this.discountRepository.findAllById(
                shopDiscounts.stream().map(OrderDiscount::getCode).collect(Collectors.toList())
        );

        if(findGlobalDiscount == null){
            throw new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND);
        }

        if(findGlobalDiscount.getType().equals(DISCOUNT_TYPE.PERCENTAGE)) {
            finalPrice = finalPrice * findGlobalDiscount.getValue() / 100;
        } else {
            finalPrice -= findGlobalDiscount.getValue();
        }

        for(Discount discount : shopDiscountLists) {
            if(discount.getType().equals(DISCOUNT_TYPE.PERCENTAGE)) {
                finalPrice = finalPrice - finalPrice * discount.getValue() / 100;
            } else {
                finalPrice = finalPrice - discount.getValue();
            }
        }

        // save order
        Order order = Order.builder()
                .orderItems(orderItemsList)
                .totalPrice(totalPrice)
                .finalTotalPrice(finalPrice)
                .totalQuantity(totalQuantity)
                .user(findUser)
                .address(findAddress)
                .status(ORDER_STATUS.PENDING)
                .paymentMethod(PAYMENT_METHOD.valueOf(paymentMethod))
                .build();
        Order savedOrder = this.orderRepository.save(order);

        // payment to get payment url
        String paymentRedirectUrl = "";

        return PlaceOrderResponse
                .builder()
                .orderId(savedOrder.getId())
                .orderStatus(order.getStatus().name())
                .paymentRedirectUrl(paymentRedirectUrl)
                .build();
    }
}
