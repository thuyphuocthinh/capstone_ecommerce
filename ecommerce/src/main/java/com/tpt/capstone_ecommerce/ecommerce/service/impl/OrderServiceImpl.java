package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.OrderDiscount;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.*;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final SkuRepository skuRepository;

    private final AddressRepository addressRepository;

    private final DiscountRepository discountRepository;

    private final UserRepository userRepository;

    public OrderServiceImpl(CartItemRepository cartItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, SkuRepository skuRepository, AddressRepository addressRepository, DiscountRepository discountRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.skuRepository = skuRepository;
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

    @Transactional(rollbackOn = BadRequestException.class)
    @Override
    public PlaceOrderResponse placeOrder(String userEmail, PlaceOrderRequest placeOrderRequest) throws BadRequestException {
        String addressId = placeOrderRequest.getAddressId();
        List<String> orderItemIds = placeOrderRequest.getOrderItemIds();
        List<OrderDiscount> shopDiscounts = placeOrderRequest.getShopDiscounts();
        OrderDiscount globalDiscounts = placeOrderRequest.getGlobalDiscounts();
        String paymentMethod = placeOrderRequest.getPaymentMethod();

        // Validate user
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        // Validate address
        Address findAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(AddressErrorConstant.ADDRESS_NOT_FOUND));

        // Validate cart items
        List<CartItem> cartItems = cartItemRepository.findAllById(orderItemIds);
        if (cartItems.size() != orderItemIds.size()) {
            throw new NotFoundException(CartErrorConstant.CART_ITEM_NOT_FOUND);
        }

        List<OrderItem> orderItemsList = new ArrayList<>();
        int totalQuantity = 0;

        for (CartItem cartItem : cartItems) {
            Sku sku = cartItem.getSku();
            if (sku.getQuantity() == 0 || sku.getQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(SkuErrorConstant.SKU_OUT_OF_STOCK);
            }

            sku.setQuantity(sku.getQuantity() - cartItem.getQuantity());
            skuRepository.save(sku);

            orderItemsList.add(
                    OrderItem.builder()
                            .quantity(cartItem.getQuantity())
                            .sku(cartItem.getSku())
                            .price(cartItem.getUnitPrice())
                            .discount(sku.getDiscount())
                            .build()
            );
            totalQuantity += cartItem.getQuantity();
        }

        // Calculate price
        double totalPrice = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        double finalPrice = cartItems.stream()
                .mapToDouble(c -> c.getTotalPrice() - c.getTotalPrice() * c.getDiscount() / 100)
                .sum();

        // Apply global discount
        Discount findGlobalDiscount = discountRepository.findByCode(globalDiscounts.getCode());
        if (findGlobalDiscount == null) {
            throw new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND);
        }

        if (findGlobalDiscount.getType().equals(DISCOUNT_TYPE.PERCENTAGE)) {
            finalPrice -= finalPrice * findGlobalDiscount.getValue() / 100;
        } else {
            finalPrice -= findGlobalDiscount.getValue();
        }

        // Apply shop discounts
        List<Discount> shopDiscountLists = discountRepository.findAllById(
                shopDiscounts.stream().map(OrderDiscount::getCode).collect(Collectors.toList())
        );

        if (shopDiscountLists.isEmpty()) {
            throw new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND);
        }

        for (Discount discount : shopDiscountLists) {
            if (discount.getType().equals(DISCOUNT_TYPE.PERCENTAGE)) {
                finalPrice -= finalPrice * discount.getValue() / 100;
            } else {
                finalPrice -= discount.getValue();
            }
        }

        // Ensure final price is not negative
        finalPrice = Math.max(finalPrice, 0);

        // Validate payment method
        PAYMENT_METHOD paymentMethodEnum;
        try {
            paymentMethodEnum = PAYMENT_METHOD.valueOf(paymentMethod);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(PaymenErrorConstant.INVALID_PAYMENT_METHOD);
        }

        // Save order
        Order order = Order.builder()
                .orderItems(orderItemsList)
                .totalPrice(totalPrice)
                .finalTotalPrice(finalPrice)
                .totalQuantity(totalQuantity)
                .user(findUser)
                .address(findAddress)
                .status(ORDER_STATUS.PENDING)
                .paymentMethod(paymentMethodEnum)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Payment process
        String paymentRedirectUrl = "";  // Implement payment logic here

        return PlaceOrderResponse.builder()
                .orderId(savedOrder.getId())
                .orderStatus(order.getStatus().name())
                .paymentRedirectUrl(paymentRedirectUrl)
                .build();
    }

    @Override
    public OrderDetailResponse getOrderDetail(String orderId) throws NotFoundException {
        Order findOrder = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));

        List<OrderItem> orderItems = findOrder.getOrderItems();
        List<OrderItemResponse> orderItemResponses = orderItems.stream().map(order -> {
            return OrderItemResponse.builder()
                    .orderItemId(order.getId())
                    .quantity(order.getQuantity())
                    .price(order.getPrice())
                    .status(order.getStatus().name())
                    .build();
        }).toList();

        int totalQuantity = orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
        double totalPrice = orderItems.stream().mapToDouble(
                order ->  order.getPrice() * order.getQuantity()
        ).sum();

        return OrderDetailResponse.builder()
                .finalPrice(findOrder.getFinalTotalPrice())
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .orderItems(orderItemResponses)
                .orderId(findOrder.getId())
                .build();
    }

    @Override
    public String cancelOrder(String orderId) throws NotFoundException, BadRequestException {
        Order findOrder = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));

        if(
                findOrder.getStatus().equals(ORDER_STATUS.DELIVERED) ||
                findOrder.getStatus().equals(ORDER_STATUS.CANCELLED) ||
                findOrder.getStatus().equals(ORDER_STATUS.PAID) ||
                findOrder.getStatus().equals(ORDER_STATUS.FAILED)
                ) {
            throw new BadRequestException(OrderErrorConstant.CANNOT_CANCEL_ORDER);
        }

        findOrder.setStatus(ORDER_STATUS.CANCELLED);

        orderRepository.save(findOrder);

        return "Success";
    }

    @Override
    public OrderItemResponse getOrderItemDetailByShop(String orderItemId) throws NotFoundException {
        OrderItem findOrderItem = this.orderItemRepository.findById(orderItemId).orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_ITEM_NOT_FOUND));

        return OrderItemResponse.builder()
                .orderItemId(findOrderItem.getId())
                .status(findOrderItem.getStatus().name())
                .quantity(findOrderItem.getQuantity())
                .price(findOrderItem.getPrice())
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListOrderByShop(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);

        Page<OrderItem> orderPage = this.orderItemRepository.findAllByShopId(shopId, page);

        List<OrderItem> orderItems = orderPage.getContent();
        List<OrderItemResponse> orderItemResponses = orderItems.stream().map(orderItem -> {
            return OrderItemResponse.builder()
                    .orderItemId(orderItem.getId())
                    .quantity(orderItem.getQuantity())
                    .price(orderItem.getPrice())
                    .status(orderItem.getStatus().name())
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalItems((int)orderPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(orderItemResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListOrderByAdmin(Integer pageNumber, Integer pageSize) throws NotFoundException {
        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);

        Page<Order> orderPage = this.orderRepository.findAll(page);
        List<Order> orders = orderPage.getContent();
        List<OrderResponse> orderResponses = orders.stream().map(order -> {
            return OrderResponse.builder()
                    .orderId(order.getId())
                    .orderStatus(order.getStatus().name())
                    .totalPrice(order.getFinalTotalPrice())
                    .totalQuantity(order.getTotalQuantity())
                    .totalPrice(order.getFinalTotalPrice())
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalItems((int)orderPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(orderResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public String updateOrderStatusByShop(String orderItemId, String statusChange) throws NotFoundException {
        return "";
    }
}
