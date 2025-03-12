package com.tpt.capstone_ecommerce.ecommerce.auth.jwt;

import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.entity.Token;
import com.tpt.capstone_ecommerce.ecommerce.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.accessToken.expiration}")
    private int jwtAccessTokenExpirationMs;
    @Value("${jwt.refreshToken.expiration}")
    private int jwtRefreshTokenExpirationMs;

    private SecretKey secretKey;

    private final TokenRepository tokenRepository;

    public JwtProvider(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Initializes the key after the class is instantiated and the jwtSecret is injected,
    // preventing the repeated creation of the key and enhancing performance
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // generate access token method
    public String generateAccessToken(Authentication authentication) {
        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()); // Chuyển về List<String>

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationMs))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .signWith(secretKey)
                .compact();
    }
    // generate refresh token
    public String generateRefreshToken(Authentication authentication) {
        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for: {}", authentication.getName());

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshTokenExpirationMs))
                .claim("email", authentication.getName())
                .signWith(secretKey)
                .compact();
    }

    // extract info from access token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        return String.valueOf(claims.get("email"));
    }

    // verify refresh token
    public Token verifyRefreshToken(String refreshToken) {
        Token refreshTokenObj = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException(UserErrorConstant.INVALID_REFRESH_TOKEN));
        if(refreshTokenObj.getExpiredAt().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        )) {
            throw new RuntimeException(UserErrorConstant.REFRESH_TOKEN_EXPIRED);
        }
        if(refreshTokenObj.isRevoked()) {
            throw new RuntimeException(UserErrorConstant.INVALID_REFRESH_TOKEN);
        }
        return refreshTokenObj;
    }

    // revoke refresh token
    public String revokeRefreshToken(String refreshToken) {
        Token refreshTokenObj = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException(UserErrorConstant.INVALID_REFRESH_TOKEN));
        if(refreshTokenObj.getExpiredAt().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        )) {
            throw new RuntimeException(UserErrorConstant.REFRESH_TOKEN_EXPIRED);
        } else {
            refreshTokenObj.setRevoked(true);
            tokenRepository.save(refreshTokenObj);
        }
        return "Success";
    }
}
