package com.tpt.capstone_ecommerce.ecommerce.auth.jwt;

import com.tpt.capstone_ecommerce.ecommerce.constant.JwtConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.entity.CustomUserDetails;
import com.tpt.capstone_ecommerce.ecommerce.service.impl.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String jwt = "";
        try {
            jwt = parseJwt(request);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid access token\"}");
            return;
        }

        if (jwt != null) {
            try {
                // GET SIGNING KEY TO PARSE
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                String email = String.valueOf(claims.get("email"));
                List<String> roles = claims.get("authorities", List.class); // Lấy thẳng List<String>
                List<GrantedAuthority> authorityList = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Thêm "ROLE_" vào trước role
                        .collect(Collectors.toList());
                // CREATE AN AUTHENTICATION
                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email); // Tải từ DB nếu cần
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorityList);
                // SAVE AUTHENTICATION TO SECURITY CONTEXT
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("After set: {}", SecurityContextHolder.getContext().getAuthentication());
            } catch (BadCredentialsException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid access token\"}");
                return;
            } catch (ExpiredJwtException ex) {
                log.error("JWT Token has expired: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Token has expired\"}");
                return;
            } catch (JwtException ex) {
                log.error("Invalid JWT Token: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid access token\"}");
                return;
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Internal Server Error\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(JwtConstant.JWT_HEADER);
        if(headerAuth == null) {
            throw new JwtException("Invalid access token");
        } else if (headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7).trim();
        }
        throw new JwtException("Invalid access token");
    }
}
