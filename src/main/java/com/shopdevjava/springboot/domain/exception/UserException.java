package com.shopdevjava.springboot.domain.exception;

/**
 * Domain exception for User-related errors
 */
public class UserException extends RuntimeException {
    private final UserExceptionType type;

    public UserException(String message, UserExceptionType type) {
        super(message);
        this.type = type;
    }

    public UserExceptionType getType() {
        return type;
    }

    public enum UserExceptionType {
        NOT_FOUND,
        DUPLICATE_EMAIL,
        VALIDATION_ERROR
    }
} 