package com.tpt.capstone_ecommerce.ecommerce.exception;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.APIErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<?> handleInvalidJwtToken(Exception ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtToken(ExpiredJwtException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(UserStatusException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }
}
