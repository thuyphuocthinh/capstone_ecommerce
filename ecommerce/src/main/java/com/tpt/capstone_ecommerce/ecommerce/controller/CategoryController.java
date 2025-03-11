package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateBrandRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCategoryRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> createBrandHandler(@Valid @ModelAttribute CreateCategoryRequest request) throws IOException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.categoryService.createCategory(request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBrandHandler(
            @PathVariable String id,
            @ModelAttribute UpdateCategoryRequest request) throws NotFoundException, IOException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.categoryService.updateCategory(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandDetailHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.categoryService.getCategoryDetail(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrandHandler(@PathVariable String id) throws NotFoundException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.categoryService.deleteCategory(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getListBrandsHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) throws NotFoundException {
        return new ResponseEntity<>(this.categoryService.getAllCategories(pageNumber, pageSize), HttpStatus.OK);
    }
}
