package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.auth.jwt.JwtProvider;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.LoginRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.LogoutRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RefreshTokenRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.RegisterRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LoginResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.LogoutResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RefreshTokenResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.RegisterResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
import com.tpt.capstone_ecommerce.ecommerce.repository.CartRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.RoleRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.TokenRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final CustomUserDetailsService customUserDetailsService;

    private final CartRepository cartRepository;

    private final TokenRepository tokenRepository;

    private final RoleRepository roleRepository;

    @Value("${jwt.refreshToken.expiration}")
    private int jwtRefreshTokenExpirationMs;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, CustomUserDetailsService customUserDetailsService, CartRepository cartRepository, TokenRepository tokenRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.cartRepository = cartRepository;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public LoginResponse loginService(LoginRequest loginRequest, String ipAddress, String userAgent) {
        // 1. Check email exists
        // 2. Compare password
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 3. Create refresh token, access token
        String accessToken = this.jwtProvider.generateAccessToken(authentication);
        String refreshToken = this.jwtProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .user(user)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();
        this.tokenRepository.save(token);

        log.info("Login::AccessToken: {}", accessToken);
        log.info("Login::RefreshToken: {}", refreshToken);

        return LoginResponse.builder().
                accessToken(accessToken).
                refreshToken(refreshToken).
                build();
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    @Override
    public RegisterResponse registerService(RegisterRequest registerRequest, String ipAddress, String userAgent) {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        // Check email exists ?
        Optional<User> findUserByEmail = this.userRepository.findByEmail(email);
        if(findUserByEmail.isPresent()) {
            throw new BadCredentialsException("Email already exists");
        }

        // Encode password
        String passwordEncoder = this.passwordEncoder.encode(password);

        // Create user
        User registeredUser = new User();
        registeredUser.setEmail(email);
        registeredUser.setPassword(passwordEncoder);
        Role role = roleRepository.findByRole(USER_ROLE.CUSTOMER);
        log.info("Register::role: {}", role);
        if (role == null) {
            throw new RuntimeException("Role CUSTOMER không tồn tại trong database");
        }
        registeredUser.setRoles(List.of(role));
        this.userRepository.save(registeredUser);

        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = this.jwtProvider.generateAccessToken(authentication);
        String refreshToken = this.jwtProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .user(registeredUser)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();
        this.tokenRepository.save(token);
        log.info("Register::AccessToken: {}", accessToken);
        log.info("Register::RefreshToken: {}", refreshToken);

        // Create cart
        Cart cart = new Cart();
        cart.setUser(registeredUser);
        this.cartRepository.save(cart);

        // Save user
        return RegisterResponse.builder().
                accessToken(accessToken).
                refreshToken(refreshToken).
                build();
    }

    @Override
    public RefreshTokenResponse refreshTokenService(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        Token verify = this.jwtProvider.verifyRefreshToken(refreshToken);

        // generate a new pair of token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String newAccessToken = this.jwtProvider.generateAccessToken(authentication);
        String newRefreshToken = this.jwtProvider.generateRefreshToken(authentication);
        verify.setRefreshToken(newRefreshToken);
        verify.setExpiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS));
        this.tokenRepository.save(verify);

        log.info("Refresh::AccessToken: {}", newAccessToken);
        log.info("Refresh::RefreshToken: {}", newRefreshToken);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public LogoutResponse logoutService(LogoutRequest logoutRequest) {
        String refreshToken = logoutRequest.getRefreshToken();
        Token verify = this.jwtProvider.verifyRefreshToken(refreshToken);

        return LogoutResponse.builder()
                .message(this.jwtProvider.revokeRefreshToken(refreshToken))
                .build();
    }
}
