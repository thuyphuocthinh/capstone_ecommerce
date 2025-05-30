package com.tpt.capstone_ecommerce.ecommerce.exception;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.APIErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getResourcePath()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(JedisException.class)
    public ResponseEntity<APIErrorResponse> handleJedisException(JedisException e) {
        APIErrorResponse response = APIErrorResponse.builder().message("REDIS SERVER UNAVAILABLE").status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<APIErrorResponse> handleTransaction(TransactionSystemException ex) {
        // ex.printStackTrace();
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HibernateException.class)
    @ResponseBody
    public ResponseEntity<String> handleHibernateException(HibernateException ex) {
        // Log exception
        return new ResponseEntity<>("An error occurred while interacting with the database", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        // Log exception
        return new ResponseEntity<>("Constraint violation error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIErrorResponse> handleRuntimeException(RuntimeException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIErrorResponse> handleNoMethodSupport(HttpRequestMethodNotSupportedException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.HTTP_VERSION_NOT_SUPPORTED
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<APIErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIErrorResponse> handleBadRequest(BadRequestException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<?> handleInvalidJwtToken(Exception ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
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
    public ResponseEntity<?> handleUserStatusException(UserStatusException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TimeExpiredException.class)
    public ResponseEntity<?> handleTimeExpiredException(TimeExpiredException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> handleMessagingException(MessagingException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        APIErrorResponse response = APIErrorResponse.builder().message(ex.getMessage()).status("Error").build();
        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }
    // AuthorizationDeniedException
}


