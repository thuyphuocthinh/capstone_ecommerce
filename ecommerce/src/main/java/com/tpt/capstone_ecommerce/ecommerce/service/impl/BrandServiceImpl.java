package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.BrandErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.BrandDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.BrandRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.BrandService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import com.tpt.capstone_ecommerce.ecommerce.utils.Slug;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    private final UploadService uploadService;

    public BrandServiceImpl(BrandRepository brandRepository, @Qualifier("cloudinary") UploadService uploadService) {
        this.brandRepository = brandRepository;
        this.uploadService = uploadService;
    }

    @Override
    public String createBrand(CreateBrandRequest createBrandRequest) throws IOException {
        Brand findBrandOrCreate = this.brandRepository.findByBrandName(createBrandRequest.getName()).orElseGet(() -> {
            Map<String, Object> uploadResult = null;
            try {
                uploadResult = this.uploadService.uploadOneFile(createBrandRequest.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");

            return Brand.builder()
                    .description(createBrandRequest.getDescription().trim())
                    .name(createBrandRequest.getName().trim().toUpperCase())
                    .imageUrl(imageUrl)
                    .slug(Slug.toSlug(createBrandRequest.getName()))
                    .build();
        });

        return this.brandRepository.save(findBrandOrCreate).getId();
    }

    @Override
    public BrandDetailResponse getBrandDetail(String id) throws NotFoundException {
        Brand brand = this.brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BrandErrorConstant.BRAND_NOT_FOUND));
        return BrandDetailResponse.builder()
                .id(brand.getId())
                .imageUrl(brand.getImageUrl())
                .description(brand.getDescription())
                .name(brand.getName())
                .slug(brand.getSlug())
                .build();
    }

    @Override
    public BrandDetailResponse updateBrand(String id, UpdateBrandRequest updateBrandRequest) throws IOException {
        Brand brand = this.brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BrandErrorConstant.BRAND_NOT_FOUND));

        String name = updateBrandRequest.getName();
        String description = updateBrandRequest.getDescription();
        MultipartFile file = updateBrandRequest.getFile();

        if(name != null) {
            brand.setName(name.trim().toUpperCase());
            brand.setSlug(Slug.toSlug(name));
        }

        if(description != null) {
            brand.setDescription(description.trim());
        }

        if(file != null) {
            Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");
            brand.setImageUrl(imageUrl);
        }

        Brand savedBrand = this.brandRepository.save(brand);

        return BrandDetailResponse.builder()
                .id(savedBrand.getId())
                .imageUrl(savedBrand.getImageUrl())
                .description(savedBrand.getDescription())
                .name(savedBrand.getName())
                .slug(savedBrand.getSlug())
                .build();
    }

    @Override
    public String deleteBrand(String id) throws NotFoundException {
        Brand brand = this.brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BrandErrorConstant.BRAND_NOT_FOUND));
        this.brandRepository.delete(brand);
        return "Success";
    }

    @Override
    public APISuccessResponseWithMetadata<?> getAllBrands(Integer pageNumber, Integer pageSize) throws NotFoundException {
        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Brand> brandsPage = this.brandRepository.findAll(page);

        log.info("addresses res:::: {}", brandsPage);

        List<Brand> brands = brandsPage.getContent();

        List<BrandDetailResponse> addressResponses = brands.stream().map(brand -> BrandDetailResponse.builder()
                .id(brand.getId())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .name(brand.getName())
                .imageUrl(brand.getImageUrl())
                .build()).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(brandsPage.getNumber() + 1)
                .pageSize(brandsPage.getSize())
                .totalPages(brandsPage.getTotalPages())
                .totalItems((int)brandsPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(addressResponses)
                .metadata(paginationMetadata)
                .build();
    }
}
