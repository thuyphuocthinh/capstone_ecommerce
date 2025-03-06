package com.tpt.capstone_ecommerce.ecommerce.service;


import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface AuthService {
    LoginResponse loginService(LoginRequest loginRequest, String ipAddress, String userAgent);
    RegisterResponse registerService(RegisterRequest registerRequest) throws MessagingException, IOException;
    TokenResponse refreshTokenService(RefreshTokenRequest refreshTokenRequest);
    LogoutResponse logoutService(LogoutRequest logoutRequest);
    TokenResponse verifyEmailServiceForAuth(String otp, String ipAddress, String userAgent);
    EmailResponse forgotPasswordSendOtpService(ForgotPasswordRequest forgotPasswordRequest) throws IOException, MessagingException;
    String verifyOtpForResetPasswordService(String otp);
    String resetPasswordService(ResetPasswordRequest resetPasswordRequest);
    LoginResponse handleLoginGoogleService(GoogleUserDTO googleUserDTO, String ipAddress, String userAgent);
}
