package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.APIErrorResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/excels")
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelService excelService;

    @GetMapping("/export")
    public ResponseEntity<?> export(HttpServletResponse response) throws IOException {
        try {
            // return Excel response
            this.excelService.exportExcel(response);
            return null;
        } catch (Exception ex) {
            // return JSON instead of letting exception bubble up
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new APIErrorResponse("ERR", "Export failed"));
        }
    }
}
