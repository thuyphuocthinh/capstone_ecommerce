package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CheckoutOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.OrderDiscount;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PlaceOrderRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_ITEM_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.*;
import com.tpt.capstone_ecommerce.ecommerce.service.CartService;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final SkuRepository skuRepository;

    private final AddressRepository addressRepository;

    private final DiscountRepository discountRepository;

    private final UserRepository userRepository;

    private final CartService cartService;

    public OrderServiceImpl(CartItemRepository cartItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, SkuRepository skuRepository, AddressRepository addressRepository, DiscountRepository discountRepository, UserRepository userRepository, CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.skuRepository = skuRepository;
        this.addressRepository = addressRepository;
        this.discountRepository = discountRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
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
                                    .skuId(cartItem.getSku().getId())
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

    @Transactional
    @Override
    public PlaceOrderResponse placeOrder(String email, PlaceOrderRequest placeOrderRequest) throws BadRequestException {
        String addressId = placeOrderRequest.getAddressId();
        List<String> orderItemIds = placeOrderRequest.getOrderItemIds();
        List<OrderDiscount> shopDiscounts = placeOrderRequest.getShopDiscounts();
        OrderDiscount globalDiscounts = placeOrderRequest.getGlobalDiscounts();
        String paymentMethod = placeOrderRequest.getPaymentMethod();

        // Validate user
        User findUser = userRepository.findByEmail(email)
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
                            .shop(cartItem.getSku().getSpu().getShop())
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
        if(globalDiscounts != null) {
            Discount findGlobalDiscount = discountRepository.findByCode(globalDiscounts.getCode());
            if (findGlobalDiscount == null) {
                throw new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND);
            }

            if (findGlobalDiscount.getType().equals(DISCOUNT_TYPE.PERCENTAGE)) {
                finalPrice -= finalPrice * findGlobalDiscount.getValue() / 100;
            } else {
                finalPrice -= findGlobalDiscount.getValue();
            }
        }

        if(shopDiscounts != null && !shopDiscounts.isEmpty()) {
            // Apply shop discounts
            List<Discount> shopDiscountLists = discountRepository.findAllByCode(
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
        // **1. Lưu Order trước**
        Order order = Order.builder()
                .totalPrice(totalPrice)
                .finalTotalPrice(finalPrice)
                .totalQuantity(totalQuantity)
                .user(findUser)
                .address(findAddress)
                .status(ORDER_STATUS.PENDING)
                .paymentMethod(paymentMethodEnum)
                .build();

        log.info("before saving order");
        Order savedOrder = orderRepository.save(order);
        log.info("after saving order");

        // **2. Gán Order vào từng OrderItem**
        for (OrderItem orderItem : orderItemsList) {
            orderItem.setOrder(savedOrder);
        }

        // **3. Lưu danh sách OrderItems vào DB**
        savedOrder.setOrderItems(orderItemsList);
        savedOrder = orderRepository.save(order);

        // clear cart
        this.cartService.clearCart(findUser.getCart().getId(), placeOrderRequest.getOrderItemIds());

        // Payment process
        String paymentRedirectUrl = "";  // Implement payment logic here

        return PlaceOrderResponse.builder()
                .orderId(savedOrder.getId())
                .orderStatus(savedOrder.getStatus().name())
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
                    .skuId(order.getSku().getId())
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
    @Transactional
    public String cancelOrder(String orderId) throws NotFoundException, BadRequestException {
        Order findOrder = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));

        if(
                findOrder.getStatus().equals(ORDER_STATUS.PARTIALLY_SHIPPED) ||
                findOrder.getStatus().equals(ORDER_STATUS.CANCELLED) ||
                findOrder.getStatus().equals(ORDER_STATUS.SHIPPED) ||
                findOrder.getStatus().equals(ORDER_STATUS.REFUNDED) ||
                findOrder.getStatus().equals(ORDER_STATUS.COMPLETED)
                ) {
            throw new BadRequestException(OrderErrorConstant.CANNOT_CANCEL_ORDER);
        }

        findOrder.setStatus(ORDER_STATUS.CANCELLED);
        List<OrderItem> orderItems = findOrder.getOrderItems();
        orderItems.forEach(orderItem -> orderItem.setStatus(ORDER_ITEM_STATUS.CANCELLED));
        orderRepository.save(findOrder);

        for (OrderItem orderItem : orderItems) {
            Sku sku = orderItem.getSku();
            sku.setQuantity(sku.getQuantity() + orderItem.getQuantity());
            skuRepository.save(sku);
        }

        return "Success";
    }

    @Override
    public OrderItemResponse getOrderItemDetailByShop(String orderItemId) throws NotFoundException {
        OrderItem findOrderItem = this.orderItemRepository.findById(orderItemId).orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_ITEM_NOT_FOUND));

        return OrderItemResponse.builder()
                .orderItemId(findOrderItem.getId())
                .skuId(findOrderItem.getSku().getId())
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
                    .skuId(orderItem.getSku().getId())
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
    public String updateOrderItemStatusByShop(String orderItemId, String statusChange) throws NotFoundException, BadRequestException {
        OrderItem orderItem = this.orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_ITEM_NOT_FOUND));

        if (ORDER_ITEM_STATUS.valueOf(statusChange) == orderItem.getStatus()) {
            throw new BadRequestException(OrderErrorConstant.ORDER_ITEM_STATUS_EXISTS);
        }

        orderItem.setStatus(ORDER_ITEM_STATUS.valueOf(statusChange));
        this.orderItemRepository.save(orderItem);

        this.updateOrderStatus(orderItem.getOrder().getId());

        return "Success";
    }

    @Override
    public String updateOrderStatus(String orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderErrorConstant.ORDER_NOT_FOUND));

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        Set<ORDER_ITEM_STATUS> itemStatuses = orderItems.stream()
                .map(OrderItem::getStatus)
                .collect(Collectors.toSet());

        if (itemStatuses.contains(ORDER_ITEM_STATUS.CANCELLED)) {
            order.setStatus(ORDER_STATUS.CANCELLED);
        } else if (itemStatuses.contains(ORDER_ITEM_STATUS.SHIPPED) && itemStatuses.contains(ORDER_ITEM_STATUS.PENDING)) {
            order.setStatus(ORDER_STATUS.PARTIALLY_SHIPPED);
        } else if (itemStatuses.contains(ORDER_ITEM_STATUS.SHIPPED) && itemStatuses.contains(ORDER_ITEM_STATUS.DELIVERED)) {
            order.setStatus(ORDER_STATUS.SHIPPED);
        } else if (itemStatuses.contains(ORDER_ITEM_STATUS.CONFIRMED)) {
            order.setStatus(ORDER_STATUS.PROCESSING);
        } else if (itemStatuses.contains(ORDER_ITEM_STATUS.DELIVERED) && itemStatuses.size() == 1) {
            order.setStatus(ORDER_STATUS.COMPLETED);
        }

        orderRepository.save(order);

        return "Success";
    }
}
