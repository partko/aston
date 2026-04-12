package com.example.usercrud.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Глобальный обработчик исключений REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        logger.warn("Пользователь не найден: {}", e.getMessage());
        ApiError error = new ApiError()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.NOT_FOUND.value())
                .setError("Resource not found")
                .setDetails(List.of(e.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Ошибка валидации: {}", e.getMessage());
        ApiError error = new ApiError()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setError("Validation error")
                .setDetails(List.of(e.getMessage()));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        ApiError error = new ApiError()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setError("Validation error")
                .setDetails(details);

        return ResponseEntity.badRequest().body(error);
    }
}