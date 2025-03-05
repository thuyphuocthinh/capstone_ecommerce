package com.tpt.capstone_ecommerce.ecommerce.controller;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.LoginRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.LogoutRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RefreshTokenRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RegisterRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponse;
import com.tpt.capstone_ecommerce.ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody @Valid RegisterRequest registerRequest,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) Optional<String> forwardedForOpt,
            HttpServletRequest request
    ) {
        log.info("Register request:::: {}", registerRequest);

        String ip = forwardedForOpt.filter(f -> !f.isEmpty()).orElse(request.getRemoteAddr());

        APISuccessResponse<Object> response = APISuccessResponse.builder()
                .data(this.authService.registerService(registerRequest, ip, userAgent))
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
}
