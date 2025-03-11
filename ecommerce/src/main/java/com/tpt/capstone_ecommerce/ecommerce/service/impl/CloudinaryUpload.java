package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tpt.capstone_ecommerce.ecommerce.constant.CloudinaryConstant;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Qualifier("cloudinary")
public class CloudinaryUpload implements UploadService {
    private final Cloudinary cloudinary;

    public CloudinaryUpload(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, Object> uploadOneFile(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }

    @Override
    public List<Map<String, Object>> uploadMultipleFiles(List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFiles.add(uploadOneFile(file));
        }
        return uploadedFiles;
    }
}
