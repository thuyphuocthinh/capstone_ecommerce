package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.HttpRequestConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.ChangePasswordRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateProfileRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.NotificationService;
import com.tpt.capstone_ecommerce.ecommerce.service.OrderService;
import com.tpt.capstone_ecommerce.ecommerce.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    private final OrderService orderService;

    private final NotificationService notificationService;

    public UserController(UserService userService, OrderService orderService, NotificationService notificationService) {
        this.userService = userService;
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfileHandler(
            @RequestHeader(HttpRequestConstant.REQUEST_AUTHORIZATION) String bearerToken
    ) {
        String accessToken = bearerToken.substring(7);
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.getUserProfile(accessToken))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PreAuthorize("@customSecurityExpression.isOwner(#id, authentication)")
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getOrdersByUserHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.getOrdersByUser(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetailByUserHandler(
            @PathVariable String id
    ) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.orderService.getOrderDetail(id))
                .message("Success")
                .build();

        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PatchMapping("/orders/{id}/cancel-order")
    public ResponseEntity<?> cancelOrderByUserHandler(
            @PathVariable String id
    ) throws NotFoundException, BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.orderService.cancelOrder(id))
                .message("Success")
                .build();

        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PreAuthorize("@customSecurityExpression.isOwner(#id, authentication)")
    @GetMapping("/{id}/addresses")
    public ResponseEntity<?> getAddressesByUserHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.getAddressesByUser(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PreAuthorize("@customSecurityExpression.isOwner(#id, authentication)")
    @PostMapping("/{id}/addresses")
    public ResponseEntity<?> createUserAddressHandler(
            @PathVariable String id,
            @RequestBody CreateUserAddressRequest createUserAddressRequest
    ) throws NotFoundException, BadRequestException {
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.createUserAddress(id, createUserAddressRequest))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PatchMapping("/addresses/{id}")
    public ResponseEntity<?> updateUserAddressHandler(
            @PathVariable String id,
            @RequestBody UpdateUserAddressRequest updateUserAddressRequest
            ) throws NotFoundException {
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.updateUserAddress(id, updateUserAddressRequest))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteUserAddressHandler(
            @PathVariable String id
    ) throws NotFoundException {
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.deleteUserAddress(id))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<?> getUserAddressDetailHandler(
            @PathVariable String id
    ) throws NotFoundException {
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.getUserAddressDetail(id))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfileHandler(
            @RequestHeader(HttpRequestConstant.REQUEST_AUTHORIZATION) String bearerToken,
            @RequestBody UpdateProfileRequest updateProfileRequest
    ) {
        String accessToken = bearerToken.substring(7);
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.updateUserProfile(accessToken, updateProfileRequest))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PatchMapping("/profile/change-password")
    public ResponseEntity<?> changePasswordHandler(
            @RequestHeader(HttpRequestConstant.REQUEST_AUTHORIZATION) String bearerToken,
            @RequestBody ChangePasswordRequest changePasswordRequest
            ) throws BadRequestException {
        String accessToken = bearerToken.substring(7);
        APISuccessResponse<Object> apiSuccessResponse = APISuccessResponse
                .builder()
                .message("Success")
                .data(this.userService.changePassword(accessToken, changePasswordRequest))
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PreAuthorize("@customSecurityExpression.isOwner(#id, authentication)")
    @GetMapping("/{id}/notifications")
    public ResponseEntity<?> getNotificationsByUserHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException, BadRequestException {
        return new ResponseEntity<>(this.notificationService.getListNotificationsByUserId(id, pageNumber, pageSize), HttpStatus.OK);
    }
}
