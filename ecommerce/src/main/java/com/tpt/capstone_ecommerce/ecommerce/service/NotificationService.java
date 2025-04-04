package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface NotificationService {
    APISuccessResponseWithMetadata<?> getListNotificationsByUserId(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getListNotificationsByShopId(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException;
    void addNewNotificationForShop(String shopId, String referenceId, NOTIFICATION_TYPE notificationType, String message) throws NotFoundException, BadRequestException;
    void addNewNotificationForUser(String userId, String referenceId, NOTIFICATION_TYPE notificationType, String message) throws NotFoundException, BadRequestException;
}
