package com.tpt.capstone_ecommerce.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UploadService {
    public Map<String, Object> uploadOneFile(MultipartFile file) throws IOException;
    public List<Map<String, Object>> uploadMultipleFiles(List<MultipartFile> files) throws IOException;
}
