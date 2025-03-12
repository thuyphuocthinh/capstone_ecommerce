package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.jpa.SkuMinPriceDTO;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SpuDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.SPU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.*;
import com.tpt.capstone_ecommerce.ecommerce.service.SpuService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import com.tpt.capstone_ecommerce.ecommerce.utils.Slug;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    private final SpuRepository spuRepository;

    private final ShopRepository shopRepository;

    private final BrandRepository brandRepository;

    private final CategoryRepository categoryRepository;

    private final UploadService uploadService;

    private final SkuRepository skuRepository;

    public SpuServiceImpl(SpuRepository spuRepository, ShopRepository shopRepository, BrandRepository brandRepository, CategoryRepository categoryRepository, @Qualifier("cloudinary") UploadService uploadService, SkuRepository skuRepository) {
        this.spuRepository = spuRepository;
        this.shopRepository = shopRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.uploadService = uploadService;
        this.skuRepository = skuRepository;
    }

    @Override
    public String createSpu(CreateSpuRequest createSpuRequest) throws NotFoundException, IOException {
        String name = createSpuRequest.getName().trim();
        String description = createSpuRequest.getDescription().trim();
        String brandId = createSpuRequest.getBrandId();
        String categoryId = createSpuRequest.getCategoryId();
        String shopId = createSpuRequest.getShopId();
        MultipartFile file = createSpuRequest.getFile();

        Shop findShop = this.shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));
        Category findCategory = this.categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));
        Brand findBrand = this.brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException(BrandErrorConstant.BRAND_NOT_FOUND));

        Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
        String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");

        String slug = Slug.toSlug(name);

        Spu newSpu = Spu.builder()
                .brand(findBrand)
                .shop(findShop)
                .category(findCategory)
                .description(description)
                .name(name)
                .slug(slug)
                .imageUrl(imageUrl)
                .build();

        return this.spuRepository.save(newSpu).getId();
    }

    @Override
    public SpuDetailResponse getSpuDetail(String id) throws NotFoundException, BadRequestException {
        Spu findSpu = this.spuRepository.findById(id).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        if(findSpu.getStatus() == SPU_STATUS.INACTIVE || findSpu.getStatus() == SPU_STATUS.DELETED) {
            throw new BadRequestException(SpuErrorConstant.SPU_STATUS_INACTIVE);
        }

        SkuMinPriceDTO findSku = this.skuRepository.findBySpuIdWithMinPrice(id).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));

        return SpuDetailResponse.builder()
                .id(findSpu.getId())
                .name(findSpu.getName())
                .description(findSpu.getDescription())
                .slug(findSpu.getSlug())
                .imageUrl(findSpu.getImageUrl())
                .brandId(findSpu.getBrand().getId())
                .brandName(findSpu.getBrand().getName())
                .categoryId(findSpu.getCategory().getId())
                .categoryName(findSpu.getCategory().getName())
                .price(findSku.getPrice())
                .discount(findSku.getDiscount())
                .build();
    }

    @Override
    public SpuDetailResponse updateSpu(String id, UpdateSpuRequest request) throws NotFoundException, IOException {
        String name = request.getName();
        String description = request.getDescription();
        String brandId = request.getBrandId();
        String categoryId = request.getCategoryId();
        MultipartFile file = request.getFile();

        Spu findSpu = this.spuRepository.findById(id).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        if (name != null) {
            findSpu.setName(name.trim());
            findSpu.setSlug(Slug.toSlug(name));
        }

        if (description != null) {
            findSpu.setDescription(description.trim());
        }

        if (brandId != null) {
            Brand findBrand = this.brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException(BrandErrorConstant.BRAND_NOT_FOUND));
            findSpu.setBrand(findBrand);
        }

        if (categoryId != null) {
            Category findCategory = this.categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));
            findSpu.setCategory(findCategory);
        }

        if (file != null) {
            Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");
            findSpu.setImageUrl(imageUrl);
        }

        Spu savedSpu = this.spuRepository.save(findSpu);

        return SpuDetailResponse.builder()
                .id(savedSpu.getId())
                .name(savedSpu.getName())
                .description(savedSpu.getDescription())
                .slug(savedSpu.getSlug())
                .imageUrl(savedSpu.getImageUrl())
                .brandId(savedSpu.getBrand().getId())
                .brandName(savedSpu.getBrand().getName())
                .categoryId(savedSpu.getCategory().getId())
                .categoryName(savedSpu.getCategory().getName())
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListsSpuHomepage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Spu> page = this.spuRepository.findAllByActiveStatus(pageable);
        List<Spu> spus = page.getContent();

        List<SpuDetailResponse> spuDetailResponses;

        spuDetailResponses = spus.stream().map(spu -> {
            SkuMinPriceDTO findSku = this.skuRepository.findBySpuIdWithMinPrice(spu.getId()).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));
            return SpuDetailResponse.builder()
                    .id(spu.getId())
                    .name(spu.getName())
                    .description(spu.getDescription())
                    .slug(spu.getSlug())
                    .imageUrl(spu.getImageUrl())
                    .brandId(spu.getBrand().getId())
                    .brandName(spu.getBrand().getName())
                    .categoryId(spu.getCategory().getId())
                    .categoryName(spu.getCategory().getName())
                    .price(findSku.getPrice())
                    .discount(findSku.getDiscount())
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
                .data(spuDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListsSpuDashboard(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Spu> page = this.spuRepository.findAllByUndeletedStatus(pageable);
        List<Spu> spus = page.getContent();

        List<SpuDetailResponse> spuDetailResponses;

        spuDetailResponses = spus.stream().map(spu -> {
            return SpuDetailResponse.builder()
                    .id(spu.getId())
                    .name(spu.getName())
                    .description(spu.getDescription())
                    .slug(spu.getSlug())
                    .imageUrl(spu.getImageUrl())
                    .brandId(spu.getBrand().getId())
                    .brandName(spu.getBrand().getName())
                    .categoryId(spu.getCategory().getId())
                    .categoryName(spu.getCategory().getName())
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
                .data(spuDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public String deleteSpu(String id, boolean isHard) throws NotFoundException, BadRequestException {
        Spu findSpu = this.spuRepository.findById(id).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        if(isHard) {
            this.spuRepository.delete(findSpu);
        } else {
            if(findSpu.getStatus() == SPU_STATUS.DELETED) {
                throw new BadRequestException(SpuErrorConstant.SPU_ALREADY_DELETED);
            }
            findSpu.setStatus(SPU_STATUS.DELETED);
            this.spuRepository.save(findSpu);
        }

        return "Success";
    }

    @Override
    public SpuDetailResponse changeSpuStatus(String id, String status) throws NotFoundException, BadRequestException {
        Spu findSpu = this.spuRepository.findById(id).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        if(findSpu.getStatus() == SPU_STATUS.valueOf(status)) {
            throw new BadRequestException(SpuErrorConstant.INVALID_STATUS);
        }

        findSpu.setStatus(SPU_STATUS.valueOf(status));
        this.spuRepository.save(findSpu);

        return SpuDetailResponse.builder()
                .id(findSpu.getId())
                .name(findSpu.getName())
                .description(findSpu.getDescription())
                .slug(findSpu.getSlug())
                .imageUrl(findSpu.getImageUrl())
                .brandId(findSpu.getBrand().getId())
                .brandName(findSpu.getBrand().getName())
                .categoryId(findSpu.getCategory().getId())
                .categoryName(findSpu.getCategory().getName())
                .build();
    }
}
