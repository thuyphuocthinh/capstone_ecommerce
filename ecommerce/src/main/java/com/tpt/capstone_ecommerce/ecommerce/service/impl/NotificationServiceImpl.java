package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.AuthConstantError;
import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.NotificationDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.OrderItemResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.entity.Notification;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.NotificationRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.NotificationService;
import com.tpt.capstone_ecommerce.ecommerce.utils.SecurityUtils;
import com.tpt.capstone_ecommerce.ecommerce.utils.WebSocketUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final ShopRepository shopRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository, ShopRepository shopRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListNotificationsByUserId(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        User findUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findUser.getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        Pageable pageRequest = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Notification> notificationPage = this.notificationRepository.findAllByUserId(findUser, pageRequest);
        List<Notification> notificationList = notificationPage.getContent();

        List<NotificationDetailResponse> notificationDetailResponses = notificationList.stream().map(notification -> {
            return NotificationDetailResponse.builder()
                    .id(notification.getId())
                    .type(notification.getType().name())
                    .message(notification.getMessage())
                    .referenceId(notification.getReferenceId())
                    .userId(findUser.getId())
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(notificationPage.getNumber() + 1)
                .pageSize(notificationPage.getSize())
                .totalPages(notificationPage.getTotalPages())
                .totalItems((int)notificationPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(notificationDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListNotificationsByShopId(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        Shop findShop = this.shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        Pageable pageRequest = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Notification> notificationPage = this.notificationRepository.findAllByShop(findShop, pageRequest);
        List<Notification> notificationList = notificationPage.getContent();

        List<NotificationDetailResponse> notificationDetailResponses = notificationList.stream().map(notification -> {
            return NotificationDetailResponse.builder()
                    .id(notification.getId())
                    .type(notification.getType().name())
                    .message(notification.getMessage())
                    .referenceId(notification.getReferenceId())
                    .userId(findShop.getId())
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(notificationPage.getNumber() + 1)
                .pageSize(notificationPage.getSize())
                .totalPages(notificationPage.getTotalPages())
                .totalItems((int)notificationPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(notificationDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public void addNewNotificationForShop(String shopId, String referenceId, NOTIFICATION_TYPE notificationType, String message) throws NotFoundException, BadRequestException {
        Shop findShop = this.shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        Notification notification = Notification.builder()
                .shop(findShop)
                .referenceId(referenceId)
                .type(notificationType)
                .message(message)
                .build();

        this.notificationRepository.save(notification);
        String destination = WebSocketUtil.getShopQueueDestination();
        messagingTemplate.convertAndSendToUser(
                findShop.getOwner().getEmail(),
                destination,
                NotificationDetailResponse.builder()
                        .id(notification.getId())
                        .type(notification.getType().name())
                        .userId(findShop.getId())
                        .referenceId(referenceId)
                        .message(message)
                        .build()
        );
    }

    @Override
    public void addNewNotificationForUser(String userId, String referenceId, NOTIFICATION_TYPE notificationType, String message) throws NotFoundException, BadRequestException {
        User findUser = this.userRepository.findByEmail(userId)
                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(findUser)
                .referenceId(referenceId)
                .type(notificationType)
                .message(message)
                .build();

        this.notificationRepository.save(notification);
        String destination = WebSocketUtil.getUserQueueDestination();
        messagingTemplate.convertAndSendToUser(
                findUser.getEmail(),
                destination,
                NotificationDetailResponse.builder()
                        .id(notification.getId())
                        .userId(findUser.getId())
                        .referenceId(referenceId)
                        .type(notification.getType().name())
                        .message(message)
                        .build()
        );
    }
}
