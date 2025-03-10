package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.HttpRequestConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.ChangePasswordRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateProfileRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getOrdersByUserHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.getOrdersByUser(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<?> getAddressesByUserHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.getAddressesByUser(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<?> createUserAddressHandler(
            @PathVariable String id,
            @RequestBody CreateUserAddressRequest createUserAddressRequest
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.createUserAddress(id, createUserAddressRequest), HttpStatus.OK);
    }

    @PatchMapping("/addresses/{id}")
    public ResponseEntity<?> updateUserAddressHandler(
            @PathVariable String id,
            @RequestBody UpdateUserAddressRequest updateUserAddressRequest
            ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.updateUserAddress(id, updateUserAddressRequest), HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteUserAddressHandler(
            @PathVariable String id
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.deleteUserAddress(id), HttpStatus.OK);
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<?> getUserAddressDetailHandler(
            @PathVariable String id
    ) throws NotFoundException {
        return new ResponseEntity<>(this.userService.getUserAddressDetail(id), HttpStatus.OK);
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
}
