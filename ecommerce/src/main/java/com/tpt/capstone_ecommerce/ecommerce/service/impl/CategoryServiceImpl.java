package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.CategoryErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;
import com.tpt.capstone_ecommerce.ecommerce.entity.Category;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.CacheCategory;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import com.tpt.capstone_ecommerce.ecommerce.repository.CategoryRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.CategoryService;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import com.tpt.capstone_ecommerce.ecommerce.utils.Slug;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final UploadService uploadService;

    private final CacheCategory cacheCategory;

    public CategoryServiceImpl(CategoryRepository categoryRepository, @Qualifier("cloudinary")UploadService uploadService, CacheCategory cacheCategory) {
        this.categoryRepository = categoryRepository;
        this.uploadService = uploadService;
        this.cacheCategory = cacheCategory;
    }

    @Override
    public String createCategory(CreateCategoryRequest request) throws IOException {
        Category findCategoryOrCreate = this.categoryRepository.findByName(request.getName()).orElseGet(() -> {
            Map<String, Object> uploadResult = null;
            try {
                uploadResult = this.uploadService.uploadOneFile(request.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");

            return Category.builder()
                    .description(request.getDescription().trim())
                    .name(request.getName().trim().toUpperCase())
                    .imageUrl(imageUrl)
                    .slug(Slug.toSlug(request.getName()))
                    .parentId(request.getParentId())
                    .build();
        });

        this.cacheCategory.incrementTotalItems(RedisSchema.getCategoryKeyItem());
        return this.categoryRepository.save(findCategoryOrCreate).getId();
    }

    @Override
    public CategoryDetailResponse getCategoryDetail(String id) throws NotFoundException {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));

        return CategoryDetailResponse.builder()
                .id(category.getId())
                .imageUrl(category.getImageUrl())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .slug(category.getSlug())
                .name(category.getName())
                .build();
    }

    @Override
    public CategoryDetailResponse updateCategory(String id, UpdateCategoryRequest request) throws NotFoundException, IOException {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));

        String name = request.getName();
        String description = request.getDescription();
        String parentId = request.getParentId();
        MultipartFile multipartFile = request.getFile();

        if(name != null) {
            category.setName(name.trim().toUpperCase());
            category.setSlug(category.getName().trim());
        }

        if(description != null) {
            category.setDescription(description.trim());
        }

        if(parentId != null) {
            category.setParentId(parentId.trim());
        }

        if(multipartFile != null) {
            Map<String, Object> uploadResult = this.uploadService.uploadOneFile(multipartFile);
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", "");
            category.setImageUrl(imageUrl);
        }

        Category savedCategory = this.categoryRepository.save(category);

        return CategoryDetailResponse.builder()
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .parentId(savedCategory.getParentId())
                .id(savedCategory.getId())
                .slug(savedCategory.getSlug())
                .imageUrl(savedCategory.getImageUrl())
                .build();
    }

    @Override
    public String deleteCategory(String id) throws NotFoundException {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));
        this.categoryRepository.delete(category);
        this.cacheCategory.decrementTotalItems(RedisSchema.getCategoryKeyItem());
        return "Success";
    }

    @Override
    public APISuccessResponseWithMetadata<?> getAllCategories(Integer pageNumber, Integer pageSize) throws NotFoundException {
        String key = RedisSchema.getCategoryKey(pageNumber);
        String totalItemsKey = RedisSchema.getCategoryKeyItem();
        PaginationMetadata paginationMetadataReturn = new PaginationMetadata();

        List<CategoryDetailResponse> categoryDetailResponses = this.cacheCategory.getListCategories(key);
        if (categoryDetailResponses.isEmpty()) {
            Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
            Page<Category> categoryPage = this.categoryRepository.findAll(page);

            List<Category> categories = categoryPage.getContent();

            categoryDetailResponses = categories.stream()
                    .map(category -> CategoryDetailResponse.builder()
                            .id(category.getId())
                            .slug(category.getSlug())
                            .description(category.getDescription())
                            .name(category.getName())
                            .imageUrl(category.getImageUrl())
                            .parentId(category.getParentId())
                            .build())
                    .collect(Collectors.toList());

            this.cacheCategory.saveListCategories(key, categoryDetailResponses);

            paginationMetadataReturn = PaginationMetadata.builder()
                    .currentPage(pageNumber)
                    .pageSize(pageSize)
                    .totalPages((int) Math.ceil((double) categoryPage.getTotalElements() / pageSize))
                    .totalItems((int) categoryPage.getTotalElements())
                    .build();

        } else {
            String totalItemsFromCache = this.cacheCategory.getTotalItems(totalItemsKey);
            if (totalItemsFromCache != null) {
                int totalItems = Integer.parseInt(totalItemsFromCache);
                paginationMetadataReturn = PaginationMetadata.builder()
                        .currentPage(pageNumber)
                        .pageSize(pageSize)
                        .totalPages((int) Math.ceil((double) totalItems / pageSize))
                        .totalItems(totalItems)
                        .build();
            }
        }
        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(categoryDetailResponses)
                .metadata(paginationMetadataReturn)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getCategoriesByParentId(String parentId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        this.categoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException(CategoryErrorConstant.CATEGORY_NOT_FOUND));

        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Category> categoryPage = this.categoryRepository.findAllByParentId(parentId, page);

        List<Category> categories = categoryPage.getContent();

        List<CategoryDetailResponse> categoryDetailResponses = categories.stream().map(category -> CategoryDetailResponse.builder()
                .id(category.getId())
                .slug(category.getSlug())
                .description(category.getDescription())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParentId())
                .build()).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(categoryPage.getNumber() + 1)
                .pageSize(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .totalItems((int)categoryPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(categoryDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getParentCategories(Integer pageNumber, Integer pageSize) throws NotFoundException {
        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Category> categoryPage = this.categoryRepository.findAllByParentId(null, page);

        log.info("addresses res:::: {}", categoryPage);

        List<Category> categories = categoryPage.getContent();

        List<CategoryDetailResponse> categoryDetailResponses = categories.stream().map(category -> CategoryDetailResponse.builder()
                .id(category.getId())
                .slug(category.getSlug())
                .description(category.getDescription())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParentId())
                .build()).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(categoryPage.getNumber() + 1)
                .pageSize(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .totalItems((int)categoryPage.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(categoryDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public List<CategoryNestedResponse> getNestedCategories() throws NotFoundException {
        // O(n^2) => oke in this case ?
        // Giam xuong O(n) => find All => map & filter
        List<Category> allCategories = this.categoryRepository.findAll();

        Map<String, List<Category>> categoryMap = allCategories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId)); // O(n)

        // Lọc danh mục cha
        return allCategories.stream()
                .filter(c -> c.getParentId() == null) // Lọc danh mục cha
                .map(parent -> {
                    List<CategoryDetailResponse> detailResponses = categoryMap.getOrDefault(parent.getId(), new ArrayList<>())
                            .stream()
                            .map(category -> CategoryDetailResponse.builder()
                                    .id(category.getId())
                                    .slug(category.getSlug())
                                    .description(category.getDescription())
                                    .name(category.getName())
                                    .imageUrl(category.getImageUrl())
                                    .parentId(category.getParentId())
                                    .build())
                            .toList();
                    return new CategoryNestedResponse(parent.getId(), parent.getName(), parent.getSlug(), detailResponses);
                })
                .toList();
    }
    // N + 1 query problem
}
