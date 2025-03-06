package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.TokenVerifier;
import com.tpt.capstone_ecommerce.ecommerce.auth.oauth.OauthProvider;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LoginResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    private final OauthProvider oauthProvider;

    private final ObjectMapper objectMapper;

    public AuthController(AuthService authService, OauthProvider oauthProvider, ObjectMapper objectMapper) {
        this.authService = authService;
        this.oauthProvider = oauthProvider;
        this.objectMapper = objectMapper;
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

    // @PostMapping("/google/verify-token")
    @PostMapping("/google/verify-token")
    public ResponseEntity<?> googleVerifyTokenHandler(
            @RequestBody GoogleTokenRequest googleTokenRequest,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) throws BadRequestException{
        GoogleUserDTO googleUserDTO = this.oauthProvider.verifyToken(googleTokenRequest.getToken());
        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());
        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.handleLoginGoogleService(googleUserDTO, ip, userAgent))
                .message("Success")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // toi ve test lai googleVerifyTokenHandler (FE gui token len BE verify => oke thi tra ve cap token, ko oke thi 401)

    @PostMapping("/google/callback")
    public void googleCallbackHandler(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        if (error != null || code == null) {
            throw new BadCredentialsException("Google login failed");
        }

        GoogleUserDTO googleUserDTO = this.oauthProvider.getUserInfoFromGoogle(code);
        Map<String, Object> stateData;
        try {
            String decodedState = new String(Base64.getDecoder().decode(state));
            stateData = objectMapper.readValue(decodedState, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Invalid state data: {}", state);
            response.sendRedirect("https://your-frontend.com/login-failed");
            throw new BadRequestException("Invalid state data");
        }

        // state tu FE phai bao gom ca user agent va ip
        String userAgent = (String) stateData.get("userAgent");
        String ip = (String) stateData.get("ip");

        LoginResponse loginResponse = this.authService.handleLoginGoogleService(googleUserDTO, ip, userAgent);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(false);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 15);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(false);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 3);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        response.sendRedirect("https://your-frontend.com/auth/callback");
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
