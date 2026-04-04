package com.example.usercrud.exception;

/**
 * Исключение, возникающее, когда сущность не найдена.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}