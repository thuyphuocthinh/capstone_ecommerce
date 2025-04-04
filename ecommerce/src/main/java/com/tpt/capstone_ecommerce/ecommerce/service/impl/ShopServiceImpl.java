package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.LocationErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.RoleErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateShopRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.ShopDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.SHOP_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.LocationRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.RoleRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.LocationService;
import com.tpt.capstone_ecommerce.ecommerce.service.ShopService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;

    private final UploadService uploadService;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    private final RoleRepository roleRepository;

    public ShopServiceImpl(ShopRepository shopRepository, @Qualifier("cloudinary") UploadService uploadService, UserRepository userRepository, LocationRepository locationRepository, LocationService locationService, RoleRepository roleRepository) {
        this.shopRepository = shopRepository;
        this.uploadService = uploadService;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
        this.roleRepository = roleRepository;
    }

    @Override
    public String createShop(CreateShopRequest createShopRequest) throws IOException {
        String ownerId = createShopRequest.getOwnerId();
        Optional<Shop> checkOwner = this.shopRepository.findByOwnerId(ownerId);

        if(checkOwner.isPresent()){
            throw new BadCredentialsException(ShopErrorConstant.SHOP_ALREADY_EXISTS_WITH_THIS_OWNER);
        }

        Location location = this.locationRepository.findById(createShopRequest.getLocationId()).orElseThrow(
                () -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND)
        );

        User user = this.userRepository.findById(createShopRequest.getOwnerId())
                .orElseThrow(
                        () -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND)
                );

        Map<String, Object> uploadResult = this.uploadService.uploadOneFile(createShopRequest.getFile());
        String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");

        Shop newShop = Shop.builder()
                .name(createShopRequest.getName().trim())
                .description(createShopRequest.getDescription().trim())
                .imageUrl(imageUrl)
                .phone(createShopRequest.getPhone().trim())
                .owner(user)
                .location(location)
                .specificAddress(createShopRequest.getSpecificAddress().trim())
                .build();

        Role role = roleRepository.findByRole(USER_ROLE.SELLER);
        log.info("Shop::role: {}", role);
        if (role == null) {
            throw new RuntimeException(RoleErrorConstant.ROLE_CUSTOMER_NOT_EXIST);
        }
        List<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        this.userRepository.save(user);

        return this.shopRepository.save(newShop).getId();
    }

    @Override
    public ShopDetailResponse updateShop(String shopId, UpdateShopRequest updateShopRequest) throws IOException, NotFoundException {
        Shop checkExist = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        if(checkExist.getStatus() == SHOP_STATUS.PENDING || checkExist.getStatus() == SHOP_STATUS.INACTIVE) {
            throw new BadRequestException(ShopErrorConstant.SHOP_BEING_INACTIVE);
        };

        String name = updateShopRequest.getName();
        String description = updateShopRequest.getDescription();
        String phone = updateShopRequest.getPhone();
        MultipartFile file = updateShopRequest.getFile();
        String specificAddress = updateShopRequest.getSpecificAddress();
        String locationId = updateShopRequest.getLocationId();

        if(name != null) {
            checkExist.setName(name.trim());
        }

        if(description != null) {
            checkExist.setDescription(description.trim());
        }

        if(phone != null) {
            checkExist.setPhone(phone.trim());
        }

        if(specificAddress != null) {
            checkExist.setSpecificAddress(specificAddress.trim());
        }

        if(locationId != null) {
            Location location = this.locationRepository.findById(locationId).orElseThrow(
                    () -> new NotFoundException(LocationErrorConstant.LOCATION_NOT_FOUND)
            );
            checkExist.setLocation(location);
        }

        if(file != null) {
            Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");
            checkExist.setImageUrl(imageUrl);
        }

        Shop updatedShop = this.shopRepository.save(checkExist);

        return ShopDetailResponse.builder()
                .id(updatedShop.getId())
                .name(updatedShop.getName())
                .description(updatedShop.getDescription())
                .phone(updatedShop.getPhone())
                .ownerId(updatedShop.getOwner().getId())
                .ownerName(updatedShop.getOwner().getFirstName() + " " + updatedShop.getOwner().getLastName())
                .address(updatedShop.getSpecificAddress() + this.locationService.getFullLocation(updatedShop.getLocation().getId()))
                .build();
    }

    @Override
    public ShopDetailResponse getShopById(String shopId) throws NotFoundException, BadRequestException {
        Shop shop = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        if(shop.getStatus() == SHOP_STATUS.PENDING || shop.getStatus() == SHOP_STATUS.INACTIVE) {
            throw new BadRequestException(ShopErrorConstant.SHOP_BEING_INACTIVE);
        };

        return ShopDetailResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .description(shop.getDescription())
                .imageUrl(shop.getImageUrl())
                .phone(shop.getPhone())
                .ownerId(shop.getOwner().getId())
                .ownerName(shop.getOwner().getFirstName() + " " + shop.getOwner().getLastName())
                .address(shop.getSpecificAddress() + ", " + this.locationService.getFullLocation(shop.getLocation().getId()))
                .build();
    }

    @Override
    public String deleteShop(String shopId) throws NotFoundException {
        Shop shop = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        this.shopRepository.delete(shop);

        return "Success";
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListShops(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Shop> shopPage = this.shopRepository.findAll(pageRequest);
        List<Shop> shops = shopPage.getContent();

        List<ShopDetailResponse> detailResponses = shops.stream().map(shop -> {
            return ShopDetailResponse.builder()
                    .id(shop.getId())
                    .name(shop.getName())
                    .description(shop.getDescription())
                    .phone(shop.getPhone())
                    .ownerId(shop.getOwner().getId())
                    .imageUrl(shop.getImageUrl())
                    .ownerName(shop.getOwner().getFirstName() + " " + shop.getOwner().getLastName())
                    .address(shop.getSpecificAddress() + ", " + this.locationService.getFullLocation(shop.getLocation().getId()))
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(shopPage.getNumber() + 1)
                .pageSize(shopPage.getSize())
                .totalPages(shopPage.getTotalPages())
                .totalItems((int)shopPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(detailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public String changeShopStatus(String shopId, String status) throws NotFoundException, BadRequestException {
        Shop shop = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));

        if(shop.getStatus().name().equals(status)) {
            throw new BadRequestException(ShopErrorConstant.INVALID_STATUS_REQUEST);
        }

        shop.setStatus(SHOP_STATUS.valueOf(status));
        this.shopRepository.save(shop);

        return "Success";
    }
}
