package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.ChangePasswordRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateProfileRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.UserAddressDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.UserProfileResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface UserService {
    UserProfileResponse getUserProfile(String accessToken);
    UserProfileResponse updateUserProfile(String accessToken, UpdateProfileRequest updateProfileRequest);
    String changePassword(String accessToken, ChangePasswordRequest changePasswordRequest) throws BadRequestException;
    APISuccessResponseWithMetadata<?> getOrdersByUser(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getAddressesByUser(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    String createUserAddress(String userId, CreateUserAddressRequest createUserAddressRequest) throws BadRequestException;
    String updateUserAddress(String addressId, UpdateUserAddressRequest updateUserAddressRequest) throws NotFoundException ;
    String deleteUserAddress(String addressId) throws NotFoundException;
    UserAddressDetailResponse getUserAddressDetail(String addressId) throws NotFoundException;
}
