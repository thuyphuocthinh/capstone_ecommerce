package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpt.capstone_ecommerce.ecommerce.auth.jwt.JwtProvider;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.ChangePasswordRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateProfileRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateUserAddressRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.entity.Address;
import com.tpt.capstone_ecommerce.ecommerce.entity.Location;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.AddressRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.LocationRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.OrderRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.LocationService;
import com.tpt.capstone_ecommerce.ecommerce.service.UserService;
import org.apache.coyote.BadRequestException;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileImpl implements UserService {
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final LocationService locationService;

    private final LocationRepository locationRepository;

    public UserProfileImpl(UserRepository userRepository, JwtProvider jwtProvider, ObjectMapper objectMapper, PasswordEncoder passwordEncoder, OrderRepository orderRepository, AddressRepository addressRepository, LocationService locationService, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.locationService = locationService;
        this.locationRepository = locationRepository;
    }

    @Override
    public UserProfileResponse getUserProfile(String accessToken) {
        String email = this.jwtProvider.getEmailFromToken(accessToken);
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));
        return new UserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateUserProfile(String accessToken, UpdateProfileRequest updateProfileRequest) {
        String email = this.jwtProvider.getEmailFromToken(accessToken);
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        String firstName = updateProfileRequest.getFirstName();
        String lastName = updateProfileRequest.getLastName();

        if(firstName != null) {
            user.setFirstName(firstName);
        }

        if(lastName != null) {
            user.setLastName(lastName);
        }

        this.userRepository.save(user);

        return new UserProfileResponse(user);
    }

    @Override
    public String changePassword(String accessToken, ChangePasswordRequest changePasswordRequest) throws BadRequestException {
        String email = this.jwtProvider.getEmailFromToken(accessToken);
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException(UserErrorConstant.OLD_PASSWORD_WRONG);
        }

        if(!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(UserErrorConstant.CHANGE_PASSWORD_DOES_NOT_MATCH);
        }

        String passwordHash = passwordEncoder.encode(newPassword);

        user.setPassword(passwordHash);

        this.userRepository.save(user);

        return "Success";
    }

    @Override
    public APISuccessReponseWithMetadata<?> getOrdersByUser(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderPage = this.orderRepository.findAllByUser(user, page);

        List<Order> orders = orderPage.getContent();

        List<UserOrderResponse> orderResponses = orders.stream().map(UserOrderResponse::new).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalItems((int)orderPage.getTotalElements())
                .build();

        return APISuccessReponseWithMetadata.builder()
                .message("Success")
                .data(orderResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessReponseWithMetadata<?> getAddressesByUser(String userId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<Address> addressesPage = this.addressRepository.findAllByUser(user, page);

        List<Address> addresses = addressesPage.getContent();

        List<UserAddressResponse> addressResponses = addresses.stream().map(address -> {
            UserAddressResponse userAddressResponse = new UserAddressResponse();
            userAddressResponse.setFullAddress(address.getSpecificAddress() + ", " + this.locationService.getFullLocation(address.getId()));
            userAddressResponse.setId(address.getId());
            userAddressResponse.setFullName(address.getFullName());
            userAddressResponse.setPhone(address.getPhone());
            return userAddressResponse;
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(addressesPage.getNumber())
                .pageSize(addressesPage.getSize())
                .totalPages(addressesPage.getTotalPages())
                .totalItems((int)addressesPage.getTotalElements())
                .build();

        return APISuccessReponseWithMetadata.builder()
                .message("Success")
                .data(addressResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public String createUserAddress(String userId, CreateUserAddressRequest createUserAddressRequest) throws NotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Location location = this.locationRepository.findById(createUserAddressRequest.getLocationId()).orElseThrow(() -> new NotFoundException("Location not found"));

        Address newAddress = new Address();
        newAddress.setUser(user);
        newAddress.setPhone(createUserAddressRequest.getPhone());
        newAddress.setSpecificAddress(createUserAddressRequest.getSpecificAddress());
        newAddress.setLocation(location);

        Address savedAddress = this.addressRepository.save(newAddress);
        return savedAddress.getId();
    }

    @Override
    public String updateUserAddress(String addressId, UpdateUserAddressRequest updateUserAddressRequest) throws NotFoundException  {
        Address address = this.addressRepository.findById(addressId).orElseThrow(() -> new NotFoundException("Address not found"));
        if(updateUserAddressRequest.getLocationId() != null) {
            Location location = this.locationRepository.findById(updateUserAddressRequest.getLocationId()).orElseThrow(() -> new NotFoundException("Location not found"));
            address.setLocation(location);
        }

        if(updateUserAddressRequest.getPhone() != null) {
            address.setPhone(updateUserAddressRequest.getPhone());
        }

        if(updateUserAddressRequest.getSpecificAddress() != null) {
            address.setSpecificAddress(updateUserAddressRequest.getSpecificAddress());
        }

        if(updateUserAddressRequest.getFullName() != null) {
            address.setFullName(updateUserAddressRequest.getFullName());
        }

        Address savedAddress = this.addressRepository.save(address);

        return savedAddress.getId();
    }

    @Override
    public String deleteUserAddress(String addressId) throws NotFoundException {
        Address address = this.addressRepository.findById(addressId).orElseThrow(() -> new NotFoundException("Address not found"));
        this.addressRepository.delete(address);
        return "Success";
    }

    @Override
    public UserAddressDetailResponse getUserAddressDetail(String addressId) throws NotFoundException {
        Address address = this.addressRepository.findById(addressId).orElseThrow(() -> new NotFoundException("Address not found"));

        List<String> listLocationIds = this.locationService.getLocationAllIds(address.getLocation().getId());

        return UserAddressDetailResponse.builder()
                .id(addressId)
                .phone(address.getPhone())
                .fullName(address.getFullName())
                .locationProvinceId(listLocationIds.get(2))
                .locationDistrictId(listLocationIds.get(1))
                .locationWardId(listLocationIds.get(0))
                .build();
    }

}
