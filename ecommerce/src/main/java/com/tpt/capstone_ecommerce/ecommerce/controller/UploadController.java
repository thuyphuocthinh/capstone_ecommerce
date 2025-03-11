package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {
    private final UploadService uploadService;

    public UploadController(@Qualifier("cloudinary") UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/res-cloudinary/file")
    public ResponseEntity<?> uploadOneFileHandler(@RequestPart MultipartFile file) throws IOException {
        return ResponseEntity.ok(uploadService.uploadOneFile(file));
    }

    @PostMapping("/res-cloudinary/files")
    public ResponseEntity<?> uploadMultipleFilesHandler(@RequestPart List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> responses = uploadService.uploadMultipleFiles(files);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/firebase/file")
    public ResponseEntity<?> uploadOneFileWithFirebaseHandler(@RequestPart MultipartFile file) throws IOException {
        return ResponseEntity.ok(uploadService.uploadOneFile(file));
    }

    @PostMapping("/firebase/files")
    public ResponseEntity<?> uploadMultipleFilesWithFirebaseHandler(@RequestPart List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> responses = uploadService.uploadMultipleFiles(files);
        return ResponseEntity.ok(responses);
    }
}
