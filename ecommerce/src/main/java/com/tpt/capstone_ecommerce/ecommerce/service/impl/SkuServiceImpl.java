package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.AuthConstantError;
import com.tpt.capstone_ecommerce.ecommerce.constant.SkuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SpuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SkuDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.SpuDetailForClientResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Sku;
import com.tpt.capstone_ecommerce.ecommerce.entity.Spu;
import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.SkuRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.SpuRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.SkuService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import com.tpt.capstone_ecommerce.ecommerce.utils.SecurityUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    private final SkuRepository skuRepository;

    private final SpuRepository spuRepository;

    private final UploadService uploadService;

    public SkuServiceImpl(SkuRepository skuRepository, SpuRepository spuRepository, @Qualifier("cloudinary") UploadService uploadService) {
        this.skuRepository = skuRepository;
        this.spuRepository = spuRepository;
        this.uploadService = uploadService;
    }

    @Override
    public String addSku(String id, CreateSkuRequest createSkuRequest) throws NotFoundException, IOException {
        String name = createSkuRequest.getName().trim();
        String size = createSkuRequest.getSize().trim();
        String color = createSkuRequest.getColor().trim();
        double price = createSkuRequest.getPrice();
        double discount = createSkuRequest.getDiscount();
        int quantity = createSkuRequest.getQuantity();
        MultipartFile file = createSkuRequest.getFile();

        Spu findSpu = this.spuRepository.findById(id).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));
        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!findSpu.getShop().getOwner().getId().equals(currentUserId)) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
        String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");

        Sku sku = Sku.builder()
                .discount(discount)
                .color(color)
                .price(price)
                .name(name)
                .size(size)
                .spu(findSpu)
                .quantity(quantity)
                .imageUrl(imageUrl)
                .build();

        return this.skuRepository.save(sku).getId();
    }

    @Override
    public SkuDetailResponse getSkuDetail(String skuId) throws NotFoundException {
        Sku findSku = this.skuRepository.findById(skuId).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));

        return SkuDetailResponse.builder()
                .id(findSku.getId())
                .color(findSku.getColor())
                .size(findSku.getSize())
                .name(findSku.getName())
                .price(findSku.getPrice())
                .quantity(findSku.getQuantity())
                .imageUrl(findSku.getImageUrl())
                .discount(findSku.getDiscount())
                .spuId(findSku.getSpu().getId())
                .status(findSku.getStatus().name())
                .build();
    }

    @Override
    public SkuDetailResponse updateSku(String skuId, UpdateSkuRequest request) throws NotFoundException, IOException {
        String name = request.getName();
        String size = request.getSize();
        String color = request.getColor();
        double price = request.getPrice();
        double discount = request.getDiscount();
        int quantity = request.getQuantity();
        MultipartFile file = request.getFile();

        Sku findSku = this.skuRepository.findById(skuId).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!findSku.getSpu().getShop().getOwner().getId().equals(currentUserId)) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        if (name != null) {
            findSku.setName(name.trim());
        }

        if (size != null) {
            findSku.setSize(size.trim());
        }

        if (color != null) {
            findSku.setColor(color.trim());
        }

        findSku.setPrice(price);
        findSku.setQuantity(quantity);
        findSku.setDiscount(discount);

        if(file != null) {
            Map<String, Object> uploadResult = this.uploadService.uploadOneFile(file);
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");
            findSku.setImageUrl(imageUrl.trim());
        }

        Sku savedSku = this.skuRepository.save(findSku);

        return SkuDetailResponse.builder()
                .id(savedSku.getId())
                .color(savedSku.getColor())
                .size(savedSku.getSize())
                .price(savedSku.getPrice())
                .quantity(savedSku.getQuantity())
                .imageUrl(savedSku.getImageUrl())
                .discount(savedSku.getDiscount())
                .spuId(savedSku.getSpu().getId())
                .build();
    }

    @Override
    public String deleteSku(String skuId, boolean isHard) throws NotFoundException, BadRequestException {
        Sku findSku = this.skuRepository.findById(skuId).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!findSku.getSpu().getShop().getOwner().getId().equals(currentUserId)) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        if(isHard) {
            this.skuRepository.delete(findSku);
        } else {
            if(findSku.getStatus() == SKU_STATUS.DELETED) {
                throw new BadRequestException(SkuErrorConstant.SKU_ALREADY_DELETED);
            }
            findSku.setStatus(SKU_STATUS.DELETED);
            this.skuRepository.save(findSku);
        }

        return "Success";
    }

    @Override
    public SpuDetailForClientResponse getListSkusForClientBySpuId(String spuId) {
        Spu findSpu = this.spuRepository.findById(spuId).orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        List<Sku> skus = this.skuRepository.findAllActiveBySpuId(spuId, SKU_STATUS.ACTIVE.name());

        List<SkuDetailResponse> responses = getSkuDetailResponses(skus);

        return SpuDetailForClientResponse.builder()
                .id(findSpu.getId())
                .name(findSpu.getName())
                .skuDetailResponses(responses)
                .build();
    }

    private List<SkuDetailResponse> getSkuDetailResponses(List<Sku> skus) {
        List<SkuDetailResponse> skuDetailResponses;
        skuDetailResponses = skus.stream().map(sku -> {
            return SkuDetailResponse.builder()
                    .id(sku.getId())
                    .color(sku.getColor())
                    .size(sku.getSize())
                    .price(sku.getPrice())
                    .quantity(sku.getQuantity())
                    .imageUrl(sku.getImageUrl())
                    .discount(sku.getDiscount())
                    .spuId(sku.getSpu().getId())
                    .name(sku.getName())
                    .status(sku.getStatus().name())
                    .build();
        }).toList();

        return skuDetailResponses;
    }

    @Override
    public List<SkuDetailResponse> getListSkusDashboardBySpuId(String spuId) {
        List<Sku> skus = this.skuRepository.findAllBySpuId(spuId);

        return getSkuDetailResponses(skus);
    }

    @Override
    public SkuDetailResponse changeStatus(String skuId, String status) throws NotFoundException, BadRequestException {
        Sku findSku = this.skuRepository.findById(skuId).orElseThrow(() -> new NotFoundException(SkuErrorConstant.SKU_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!findSku.getSpu().getShop().getOwner().getId().equals(currentUserId)) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        if(findSku.getStatus() == SKU_STATUS.valueOf(status)) {
            throw new BadRequestException(SpuErrorConstant.INVALID_STATUS);
        }

        findSku.setStatus(SKU_STATUS.valueOf(status));
        Sku savedSku = this.skuRepository.save(findSku);

        return SkuDetailResponse.builder()
                .id(savedSku.getId())
                .color(savedSku.getColor())
                .size(savedSku.getSize())
                .price(savedSku.getPrice())
                .quantity(savedSku.getQuantity())
                .imageUrl(savedSku.getImageUrl())
                .discount(savedSku.getDiscount())
                .spuId(savedSku.getSpu().getId())
                .name(savedSku.getName())
                .status(savedSku.getStatus().name())
                .build();
    }
}
