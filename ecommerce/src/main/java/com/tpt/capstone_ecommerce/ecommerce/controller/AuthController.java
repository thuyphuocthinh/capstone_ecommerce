package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello message";
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(
            @RequestBody @Valid LoginRequest loginRequest,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) {
        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());

        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.loginService(loginRequest, ip, userAgent))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(
            @RequestBody @Valid RegisterRequest registerRequest
    ) throws MessagingException, IOException {
        log.info("Register request:::: {}", registerRequest);

        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.registerService(registerRequest))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenHandler(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
            ) {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.refreshTokenService(refreshTokenRequest))
                .message("Success")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutHandler(@Valid @RequestBody LogoutRequest logoutRequest) {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.logoutService(logoutRequest))
                .message("Success")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmailHandler(
            @RequestParam(value = "otp", required = true) String otp,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) {
        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.verifyEmailServiceForAuth(otp, ip, userAgent))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> forgotPasswordSendOtpHandler(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest
            ) throws MessagingException, IOException {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.forgotPasswordSendOtpService(forgotPasswordRequest))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // verify-otp
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> forgotPasswordVerifyOtpHandler(
            @RequestBody VerifyOtpRequest verifyOtpRequest
    )  {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.verifyOtpForResetPasswordService(verifyOtpRequest.getOtp()))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // reset-password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> forgotPasswordResetHandler(
            @RequestBody ResetPasswordRequest resetPasswordRequest
    )  {
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.resetPasswordService(resetPasswordRequest))
                .message("Success")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
