package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.auth.jwt.JwtProvider;
import com.tpt.capstone_ecommerce.ecommerce.constant.RoleErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.*;
import com.tpt.capstone_ecommerce.ecommerce.entity.*;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.exception.UserStatusException;
import com.tpt.capstone_ecommerce.ecommerce.repository.*;
import com.tpt.capstone_ecommerce.ecommerce.service.AuthService;
import com.tpt.capstone_ecommerce.ecommerce.service.EmailService;
import com.tpt.capstone_ecommerce.ecommerce.service.OtpService;
import com.tpt.capstone_ecommerce.ecommerce.utils.Template;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private final EmailService emailService;

    private final TokenRepository tokenRepository;

    private final RoleRepository roleRepository;

    private final OtpService otpService;

    private final OtpRepository otpRepository;

    private final AuthProviderRepository authProviderRepository;

    @Value("${jwt.refreshToken.expiration}")
    private int jwtRefreshTokenExpirationMs;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, CustomUserDetailsService customUserDetailsService, CartRepository cartRepository, EmailService emailService, TokenRepository tokenRepository, RoleRepository roleRepository, OtpService otpService, OtpRepository otpRepository, AuthProviderRepository authProviderRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.cartRepository = cartRepository;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.otpService = otpService;
        this.otpRepository = otpRepository;
        this.authProviderRepository = authProviderRepository;
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

        if(user.getStatus() == USER_STATUS.PENDING) {
            throw new UserStatusException(UserErrorConstant.USER_PENDING_STATUS);
        }

        if(user.getStatus() == USER_STATUS.INACTIVE) {
            throw new UserStatusException(UserErrorConstant.USER_INACTIVE_STATUS);
        }

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
            throw new BadCredentialsException(UserErrorConstant.INVALID_CREDENTIALS);
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(UserErrorConstant.INVALID_CREDENTIALS);
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    @Override
    public RegisterResponse registerService(RegisterRequest registerRequest) throws MessagingException, IOException {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        // Check email exists ?
        Optional<User> findUserByEmail = this.userRepository.findByEmail(email);
        if(findUserByEmail.isPresent()) {
            throw new BadCredentialsException(UserErrorConstant.EMAIL_ALREADY_EXISTS);
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
            throw new RuntimeException(RoleErrorConstant.ROLE_CUSTOMER_NOT_EXIST);
        }
        registeredUser.setRoles(List.of(role));
        this.userRepository.save(registeredUser);

        String otp = this.otpService.generateOtp(email);
        String template = Template.getOtpHtmlTemplateAuth(otp);
        this.emailService.sendEmailWithHtml(email, "VERIFY EMAIL TPT_SHOP", template);

        return RegisterResponse.builder()
                .message("OTP was successfully sent to you email. Please verify.")
                .build();
    }

    @Override
    public TokenResponse refreshTokenService(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        Token verify = this.jwtProvider.verifyRefreshToken(refreshToken);
        Token getFromDb = this.tokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getFromDb.getUser().getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        String newAccessToken = this.jwtProvider.generateAccessToken(authentication);
        String newRefreshToken = this.jwtProvider.generateRefreshToken(authentication);
        verify.setRefreshToken(newRefreshToken);
        verify.setExpiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS));
        this.tokenRepository.save(verify);

        log.info("Refresh::AccessToken: {}", newAccessToken);
        log.info("Refresh::RefreshToken: {}", newRefreshToken);

        return TokenResponse.builder()
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

    @Override
    public TokenResponse verifyEmailServiceForAuth(String otp, String ipAddress, String userAgent) {
        // Lấy email từ OTP
        String email = this.verifyEmailService(otp);

        // Tìm User bằng email
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User userFromDb = user.get();
        userFromDb.setStatus(USER_STATUS.ACTIVE);
        this.userRepository.save(userFromDb);

        // Tạo đối tượng `Authentication`
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo AccessToken & RefreshToken từ `authentication`
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .user(user.get())
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();

        this.tokenRepository.save(token);

        Cart cart = new Cart();
        cart.setUser(user.get());
        this.cartRepository.save(cart);

        return TokenResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }

    @Override
    public EmailResponse forgotPasswordSendOtpService(ForgotPasswordRequest forgotPasswordRequest) throws IOException, MessagingException {
        String email = forgotPasswordRequest.getEmail();

        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        if(user.getStatus() == USER_STATUS.PENDING) {
            throw new UserStatusException(UserErrorConstant.USER_PENDING_STATUS);
        }

        if(user.getStatus() == USER_STATUS.INACTIVE) {
            throw new UserStatusException(UserErrorConstant.USER_INACTIVE_STATUS);
        }

        String otp = this.otpService.generateOtp(email);
        String template = Template.getOtpHtmlTemplateForgot(otp);
        this.emailService.sendEmailWithHtml(email, "VERIFY EMAIL TPT_SHOP", template);
        return EmailResponse.builder()
                .message("OTP was successfully sent to you email. Please verify.")
                .build();
    }

    @Override
    public String verifyOtpForResetPasswordService(String otp) {
        this.otpService.validateOtp(otp);
        return "Success";
    }

    @Override
    public String resetPasswordService(ResetPasswordRequest resetPasswordRequest) {
        String password = resetPasswordRequest.getPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();
        String otp = resetPasswordRequest.getOtp();

        if(!password.equals(confirmPassword)) {
            throw new BadCredentialsException("Passwords do not match");
        }

        Otp findOtp = this.otpRepository.findByOtp(otp);

        if(findOtp == null) {
            throw new NotFoundException("Invalid request");
        }

        if(findOtp.getExpiredAt().isBefore(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))) {
            throw new NotFoundException("Invalid request");
        }

        User user = this.userRepository.findByEmail(findOtp.getUserEmail()).orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));
        user.setPassword(this.passwordEncoder.encode(password));
        this.userRepository.save(user);

        return "Success";
    }

    private void saveAuthProvider(GoogleUserDTO googleUserDTO, User user) {
        AuthProvider authProvider = AuthProvider.builder()
                .providerId(googleUserDTO.getSub())
                .email(googleUserDTO.getEmail())
                .user(user)
                .build();

        this.authProviderRepository.save(authProvider);
    }

    @Override
    public LoginResponse handleLoginGoogleService(GoogleUserDTO googleUserDTO, String ipAddress, String userAgent) {
        String email = googleUserDTO.getEmail();

        User createdOrExist = this.userRepository.findByEmail(email).orElseGet(() -> {
            Role role = roleRepository.findByRole(USER_ROLE.CUSTOMER);
            User newUser = User.builder()
                    .email(email)
                    .firstName(googleUserDTO.getGivenName())
                    .lastName(googleUserDTO.getFamilyName())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .status(USER_STATUS.ACTIVE)
                    .roles(List.of(role))
                    .build();
            User savedUser = this.userRepository.save(newUser);

            Cart cart = new Cart();
            cart.setUser(savedUser);
            this.cartRepository.save(cart);

            this.saveAuthProvider(googleUserDTO, savedUser);

            return savedUser;
        });

        AuthProvider findAuthProvider = this.authProviderRepository.findByEmail(email);
        if(findAuthProvider == null) {
            this.saveAuthProvider(googleUserDTO, createdOrExist);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo AccessToken & RefreshToken từ `authentication`
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        Token token = Token.builder()
                .user(createdOrExist)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiredAt(LocalDateTime.now().plus(jwtRefreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();
        this.tokenRepository.save(token);

        return LoginResponse.builder().
                accessToken(accessToken).
                refreshToken(refreshToken).
                build();
    }

    public String verifyEmailService(String otp) {
        this.otpService.validateOtp(otp);
        Otp findOtp = this.otpRepository.findByOtp(otp);

        if(findOtp == null) {
            throw new NotFoundException("Otp not found");
        }

        return findOtp.getUserEmail();
    }
}
