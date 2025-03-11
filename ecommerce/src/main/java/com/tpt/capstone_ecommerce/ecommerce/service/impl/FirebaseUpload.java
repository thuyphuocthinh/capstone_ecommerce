package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.tpt.capstone_ecommerce.ecommerce.service.UploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Qualifier("firebase")
public class FirebaseUpload implements UploadService {
    @Override
    public Map<String, Object> uploadOneFile(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        String downloadUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                fileName.replace("/", "%2F"));

        Map<String, Object> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("url", downloadUrl);
        response.put("contentType", file.getContentType());
        response.put("size", file.getSize());

        return response;
    }

    @Override
    public List<Map<String, Object>> uploadMultipleFiles(List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(uploadOneFile(file));
        }
        return result;
    }
}
