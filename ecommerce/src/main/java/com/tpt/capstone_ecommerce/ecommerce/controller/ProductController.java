package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.constant.AppConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SkuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.SpuErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSkuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateSpuRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SPU_STATUS;
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

    public ProductController(SpuService spuService, SkuService skuService) {
        this.spuService = spuService;
        this.skuService = skuService;
    }

    @PostMapping("/spus")
    public ResponseEntity<?> createSpuHandler(@Valid @ModelAttribute CreateSpuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.createSpu(request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
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

    @PatchMapping("/spus/{id}")
    public ResponseEntity<?> updateSpuHandler(@PathVariable String id, @ModelAttribute UpdateSpuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.updateSpu(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/{id}/change-status/{status}")
    public ResponseEntity<?> updateSpuStatusHandler(@PathVariable String id, @PathVariable String status) throws IOException {
        if(!status.equals(SPU_STATUS.ACTIVE.name()) && !status.equals(SPU_STATUS.INACTIVE.name())) {
            throw new BadRequestException(SpuErrorConstant.INVALID_STATUS);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.changeSpuStatus(id, status))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/spus/{id}/hard/{isHard}")
    public ResponseEntity<?> deleteSpuHandler(@PathVariable String id, @PathVariable Boolean isHard) throws BadRequestException {
        if(isHard == null){
            throw new BadRequestException(SpuErrorConstant.LACK_IS_HARD);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(spuService.deleteSpu(id, isHard))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/spus/{id}/skus")
    public ResponseEntity<?> createSkuHandler(@Valid @ModelAttribute CreateSkuRequest request, @PathVariable String id) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.addSku(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
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

    @PatchMapping("/spus/skus/{id}")
    public ResponseEntity<?> updateSkuHandler(@PathVariable String id, @ModelAttribute UpdateSkuRequest request) throws IOException {
        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.updateSku(id, request))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/spus/skus/{id}/hard/{isHard}")
    public ResponseEntity<?> deleteSkuHandler(@PathVariable String id, @PathVariable Boolean isHard) throws BadRequestException {
        if(isHard == null){
            throw new BadRequestException(SpuErrorConstant.LACK_IS_HARD);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.deleteSku(id, isHard))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/spus/skus/{id}/change-status/{status}")
    public ResponseEntity<?> updateSkuStatusHandler(@PathVariable String id, @PathVariable String status) throws IOException {
        if(!status.equals(SKU_STATUS.ACTIVE.name()) && !status.equals(SKU_STATUS.INACTIVE.name())) {
            throw new BadRequestException(SkuErrorConstant.INVALID_STATUS);
        }

        APISuccessResponse<Object> apiResponse = APISuccessResponse.builder()
                .data(skuService.changeStatus(id, status))
                .message("Success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
