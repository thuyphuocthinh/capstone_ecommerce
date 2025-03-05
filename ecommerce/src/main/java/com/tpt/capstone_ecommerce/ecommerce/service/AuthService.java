package com.tpt.capstone_ecommerce.ecommerce.service;


import com.tpt.capstone_ecommerce.ecommerce.dto.request.LoginRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.LogoutRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RefreshTokenRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RegisterRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LoginResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LogoutResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RefreshTokenResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse loginService(LoginRequest loginRequest, String ipAddress, String userAgent);
    RegisterResponse registerService(RegisterRequest registerRequest, String ipAddress, String userAgent);
    RefreshTokenResponse refreshTokenService(RefreshTokenRequest refreshTokenRequest);
    LogoutResponse logoutService(LogoutRequest logoutRequest);
}
