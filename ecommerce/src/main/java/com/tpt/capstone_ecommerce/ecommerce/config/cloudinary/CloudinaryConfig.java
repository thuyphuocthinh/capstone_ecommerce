package com.tpt.capstone_ecommerce.ecommerce.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tpt.capstone_ecommerce.ecommerce.constant.CloudinaryConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                CloudinaryConstant.CLOUD_NAME, cloudName,
                CloudinaryConstant.API_KEY, apiKey,
                CloudinaryConstant.API_SECRET, apiSecret
        ));
    }
}
