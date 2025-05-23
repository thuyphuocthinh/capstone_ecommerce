package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SkuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SpuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SPU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.service.CommentService;
import com.tpt.capstone_ecommerce.ecommerce.service.SkuService;
import com.tpt.capstone_ecommerce.ecommerce.service.SpuService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final SpuService spuService;

    private final SkuService skuService;

    private final CommentService commentService;

    public ProductController(SpuService spuService, SkuService skuService, CommentService commentService) {
        this.spuService = spuService;
        this.skuService = skuService;
        this.commentService = commentService;
    }

    @GetMapping("/spus/{id}")
    public ResponseEntity<?> getSpuDetailHandler(@PathVariable String id) throws BadRequestException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.getSpuDetail(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/spus-home")
    public ResponseEntity<?> getSpuHomepageHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.spuService.getListsSpuHomepage(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus-dashboard")
    public ResponseEntity<?> getSpuDashboardHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.spuService.getListsSpuDashboard(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus/brands/{id}")
    public ResponseEntity<?> getSpuByBrandHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.spuService.getListsSpuByBrand(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus/categories/{id}")
    public ResponseEntity<?> getSpuByCategoryHandler(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.spuService.getListsSpuByCategory(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus")
    public ResponseEntity<?> searchSpusHandler(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false, defaultValue = AppConstant.SORT_BY) String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = AppConstant.SORT_DIRECTION_ASC) String sortDirection,
            @RequestParam(name = "brandIds", required = false) String brandIds,
            @RequestParam(name = "categoryIds", required = false) String categoryIds,
            @RequestParam(name = "keyword", required = true) String name
    ) throws BadRequestException {
        return new ResponseEntity<>(this.spuService.searchSpuByName(name, brandIds, categoryIds, sortBy, sortDirection,  pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus/skus/{id}")
    public ResponseEntity<?> getSkuDetailHandler(@PathVariable String id) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.getSkuDetail(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/spus/{id}/skus")
    public ResponseEntity<?> getListSkusBySpuHandler(@PathVariable String id) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.getListSkusForClientBySpuId(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/spus/{id}/comments")
    public ResponseEntity<?> addCommentHandler(@PathVariable String id, @Valid @RequestBody CreateCommentRequest createCommentRequest) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.commentService.createComment(id, createCommentRequest))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PostMapping("/spus/comments/{id}/replies")
    public ResponseEntity<?> replyCommentHandler(@PathVariable String id, @Valid @RequestBody CreateCommentRequest createCommentRequest) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.commentService.replyComment(id, createCommentRequest))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/comments/{id}")
    public ResponseEntity<?> updateCommentHandler(@PathVariable String id, @Valid @RequestBody UpdateCommentRequest updateCommentRequest) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.commentService.updateComment(id, updateCommentRequest))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @DeleteMapping("/spus/comments/{id}")
    public ResponseEntity<?> deleteCommentHandler(@PathVariable String id) throws BadRequestException {
        APISuccessResponse<?> apiSuccessResponse = APISuccessResponse.builder()
                .data(this.commentService.deleteComment(id))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiSuccessResponse, HttpStatus.OK);
    }

    @GetMapping("/spus/{id}/comments")
    public ResponseEntity<?> getCommentsBySpu(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.commentService.getListOfCommentsBySpu(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/spus/comments/{id}/replies")
    public ResponseEntity<?> getRepliesOfComment(
            @PathVariable String id,
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstant.PAGE_SIZE) Integer pageSize
    ) {
        return new ResponseEntity<>(this.commentService.getRepliesOfComment(id, pageNumber, pageSize), HttpStatus.OK);
    }
}
