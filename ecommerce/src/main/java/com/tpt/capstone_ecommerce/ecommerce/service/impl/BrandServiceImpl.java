package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.BrandErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.BrandDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.CacheBrand;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import com.tpt.capstone_ecommerce.ecommerce.repository.BrandRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.BrandService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import com.tpt.capstone_ecommerce.ecommerce.utils.Slug;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    private final UploadService uploadService;

    private final CacheBrand cacheBrand;

    public BrandServiceImpl(BrandRepository brandRepository, @Qualifier("cloudinary") UploadService uploadService, CacheBrand cacheBrand) {
        this.brandRepository = brandRepository;
        this.uploadService = uploadService;
        this.cacheBrand = cacheBrand;
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

        Brand brand = this.brandRepository.save(findBrandOrCreate);
        String key = RedisSchema.getBrandKey();
        this.cacheBrand.addNewBrand(key, brand);
        return brand.getId();
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
        String key = RedisSchema.getBrandKey();
        this.cacheBrand.updateBrand(key, id, savedBrand);

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
        String key = RedisSchema.getBrandKey();
        this.cacheBrand.removeBrand(key, id);
        return "Success";
    }

    @Override
    public List<BrandDetailResponse> getAllBrands() throws NotFoundException {
        String key = RedisSchema.getBrandKey();
        List<BrandDetailResponse> brandDetailResponses = this.cacheBrand.getListCategories(key);
        log.info("Brand from cache: {}", brandDetailResponses);
        if (brandDetailResponses.isEmpty()) {
            List<Brand> brands = this.brandRepository.findAll();

            brandDetailResponses = brands.stream()
                    .map(brand -> BrandDetailResponse.builder()
                            .id(brand.getId())
                            .slug(brand.getSlug())
                            .description(brand.getDescription())
                            .name(brand.getName())
                            .imageUrl(brand.getImageUrl())
                            .build())
                    .collect(Collectors.toList());

            this.cacheBrand.saveListBrands(key, brandDetailResponses);
        }
        return brandDetailResponses;
    }
}
