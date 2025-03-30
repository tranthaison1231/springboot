package com.shopdevjava.springboot.application.exception;

import com.shopdevjava.springboot.domain.exception.UserException;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(UserException ex) {
        HttpStatus status;
        
        switch (ex.getType()) {
            case NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                break;
            case DUPLICATE_EMAIL:
                status = HttpStatus.BAD_REQUEST;
                break;
            case VALIDATION_ERROR:
                status = HttpStatus.BAD_REQUEST;
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), status);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return new ResponseEntity<>(
            ApiResponse.error("Validation failed"),
            HttpStatus.BAD_REQUEST
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
            ApiResponse.error("An unexpected error occurred: " + ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
} 