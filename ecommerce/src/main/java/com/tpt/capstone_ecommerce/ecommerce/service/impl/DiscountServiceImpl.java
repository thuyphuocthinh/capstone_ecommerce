package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.AuthConstantError;
import com.tpt.capstone_ecommerce.ecommerce.constant.DiscountErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateDiscountRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.DiscountDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.entity.Discount;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.DiscountRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.DiscountService;
import com.tpt.capstone_ecommerce.ecommerce.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    private final UserRepository userRepository;

    private final ShopRepository shopRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository, UserRepository userRepository, ShopRepository shopRepository) {
        this.discountRepository = discountRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public String createDiscount(String shopId, CreateDiscountRequest request) throws RuntimeException, BadRequestException {
        String name = request.getName().trim();
        String description = request.getDescription().trim();
        String code = request.getCode().trim();
        String type = request.getType().trim();
        double value = request.getValue();
        double minOrderValue = request.getMinOrderValue();
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        Shop findShop = this.shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));
        User findCreator = findShop.getOwner();

        Discount findDiscount = this.discountRepository.findByCode(code);
        if(findDiscount != null) {
            throw new BadRequestException(DiscountErrorConstant.DISCOUNT_CODE_ALREADY_EXISTS);
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException(DiscountErrorConstant.START_DATE_AFTER_END_DATE);
        }

        Discount newDiscount = Discount.builder()
                .code(code)
                .name(name)
                .creator(findCreator)
                .description(description)
                .startDate(startDate)
                .isGlobal(findCreator.getRoles().stream().map(role -> role.getRole().name()).toList().contains(USER_ROLE.ADMIN.name()))
                .endDate(endDate)
                .value(value)
                .minOrderValue(minOrderValue)
                .type(DISCOUNT_TYPE.valueOf(type))
                .build();

        try {
            return this.discountRepository.save(newDiscount).getId();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi không xác định: " + e.getMessage());
        }

    }

    @Override
    public DiscountDetailResponse getDiscountDetail(String discountId) throws NotFoundException, BadRequestException {
        Discount findDiscount = this.discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findDiscount.getCreator().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        return DiscountDetailResponse.builder()
                .id(findDiscount.getId())
                .name(findDiscount.getName())
                .description(findDiscount.getDescription())
                .value(findDiscount.getValue())
                .minOrderValue(findDiscount.getMinOrderValue())
                .startDate(findDiscount.getStartDate())
                .endDate(findDiscount.getEndDate())
                .code(findDiscount.getCode())
                .creatorId(findDiscount.getCreator().getId())
                .type(findDiscount.getType().name())
                .creatorName(findDiscount.getCreator().getFirstName() + " " + findDiscount.getCreator().getLastName())
                .build();
    }

    @Override
    public DiscountDetailResponse updateDiscount(String discountId, UpdateDiscountRequest request) throws NotFoundException, BadRequestException {
        Discount findDiscount = this.discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findDiscount.getCreator().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        String name = request.getName();
        String description = request.getDescription();
        String code = request.getCode();
        String type = request.getType();
        Double value = request.getValue();
        Double minOrderValue = request.getMinOrderValue();
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        if(name != null) {
            findDiscount.setName(name);
        }

        if(description != null) {
            findDiscount.setDescription(description);
        }

        if(type != null) {
            findDiscount.setType(DISCOUNT_TYPE.valueOf(type));
        }

        if(value != null) {
            findDiscount.setValue(value);
        }

        if(minOrderValue != null) {
            findDiscount.setMinOrderValue(minOrderValue);
        }

        if(code != null) {
            if(findDiscount.getCode().equals(code)) {
                throw new BadRequestException(DiscountErrorConstant.DISCOUNT_CODE_ALREADY_EXISTS);
            }
            findDiscount.setCode(code);
        }

        if(startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new BadRequestException(DiscountErrorConstant.START_DATE_AFTER_END_DATE);
            }
            findDiscount.setStartDate(startDate);
            findDiscount.setEndDate(endDate);
        } else if (startDate == null && endDate != null) {
            if(findDiscount.getStartDate().isAfter(endDate)) {
                throw new BadRequestException(DiscountErrorConstant.START_DATE_AFTER_END_DATE);
            }
            findDiscount.setEndDate(endDate);
        } else if (startDate != null) {
            if(startDate.isAfter(findDiscount.getEndDate())) {
                throw new BadRequestException(DiscountErrorConstant.START_DATE_AFTER_END_DATE);
            }
            findDiscount.setStartDate(startDate);
        }

        Discount updated = this.discountRepository.save(findDiscount);

        return DiscountDetailResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .value(updated.getValue())
                .minOrderValue(updated.getMinOrderValue())
                .startDate(updated.getStartDate())
                .endDate(updated.getEndDate())
                .code(updated.getCode())
                .creatorId(updated.getCreator().getId())
                .type(findDiscount.getType().name())
                .creatorName(updated.getCreator().getFirstName() + " " + updated.getCreator().getLastName())
                .build();
    }

    @Override
    public String deleteDiscount(String discountId) throws NotFoundException, BadRequestException {
        Discount findDiscount = this.discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findDiscount.getCreator().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        this.discountRepository.delete(findDiscount);
        return "Success";
    }

    @Override
    public APISuccessResponseWithMetadata<?> getDiscountsByCreator(String shopId, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);

        Shop findShop = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));
        User findCreator = findShop.getOwner();

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findCreator.getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        Page<Discount> page = this.discountRepository.findAllByCreator(findCreator, pageRequest);
        return getApiSuccessResponseWithMetadata(page);
    }

    @Override
    public APISuccessResponseWithMetadata<?> getAllDiscounts(Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Discount> page = this.discountRepository.findAll(pageRequest);
        return getApiSuccessResponseWithMetadata(page);
    }

    @Override
    public String changeDiscountStatus(String discountId, String status) throws NotFoundException, BadRequestException {
        Discount findDiscount = this.discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException(DiscountErrorConstant.DISCOUNT_NOT_FOUND));

        if(findDiscount.getStatus().equals(DISCOUNT_STATUS.valueOf(status))) {
            throw new BadRequestException(DiscountErrorConstant.DISCOUNT_WITH_STATUS_ALREADY_EXISTS);
        }

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findDiscount.getCreator().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        findDiscount.setStatus(DISCOUNT_STATUS.valueOf(status));
        this.discountRepository.save(findDiscount);

        return "Success";
    }

    @Override
    public APISuccessResponseWithMetadata<?> getDiscountsByAmountAndShop(String shopId, Double amount, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        Pageable pageRequest = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);

        Page<Discount> page = this.discountRepository.findAllByShopIdAndValue(shopId, amount, pageRequest);
        return getApiSuccessResponseWithMetadata(page);
    }

    @Override
    public APISuccessResponseWithMetadata<?> getGlobalDiscountsByAmount(Double amount, Integer pageNumber, Integer pageSize) throws NotFoundException, BadRequestException {
        Pageable pageRequest = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Discount> page = this.discountRepository.findAllGlobalDiscountsWithAmount(amount, pageRequest);
        return getApiSuccessResponseWithMetadata(page);
    }

    private APISuccessResponseWithMetadata<?> getApiSuccessResponseWithMetadata(Page<Discount> page) {
        List<Discount> discounts = page.getContent();
        List<DiscountDetailResponse> detailResponses;

        detailResponses = discounts.stream().map(discount -> {
            return DiscountDetailResponse.builder()
                    .id(discount.getId())
                    .name(discount.getName())
                    .description(discount.getDescription())
                    .value(discount.getValue())
                    .minOrderValue(discount.getMinOrderValue())
                    .startDate(discount.getStartDate())
                    .endDate(discount.getEndDate())
                    .code(discount.getCode())
                    .creatorId(discount.getCreator().getId())
                    .type(discount.getType().name())
                    .creatorName(discount.getCreator().getFirstName() + " " + discount.getCreator().getLastName())
                    .build();
        }).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalItems((int)page.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(detailResponses)
                .metadata(paginationMetadata)
                .build();
    }
}
