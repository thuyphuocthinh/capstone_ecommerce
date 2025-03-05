package com.tpt.capstone_ecommerce.ecommerce.auth.jwt;

import com.tpt.capstone_ecommerce.ecommerce.constant.JwtConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = parseJwt(request);

        log.debug("JWT found: {}", jwt != null ? "Yes" : "No");

        if (jwt != null) {
            try {
                // GET SIGNING KEY TO PARSE
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                String email = String.valueOf(claims.get("email"));
                List<String> roles = claims.get("authorities", List.class); // Lấy thẳng List<String>
                List<GrantedAuthority> authorityList = roles.stream()
                        .map(SimpleGrantedAuthority::new) // Chuyển thành GrantedAuthority
                        .collect(Collectors.toList());
                // CREATE AN AUTHENTICATION
                Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorityList);
                log.info("authorityList: {}", authorityList);
                // SAVE AUTHENTICATION TO SECURITY CONTEXT
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException(UserErrorConstant.INVALID_ACCESS_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(JwtConstant.JWT_HEADER);
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
