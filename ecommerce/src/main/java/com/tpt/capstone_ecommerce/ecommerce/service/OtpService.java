package com.tpt.capstone_ecommerce.ecommerce.service;

public interface OtpService {
    boolean validateOtp(String otp);
    String generateOtp(String userEmail);
}
