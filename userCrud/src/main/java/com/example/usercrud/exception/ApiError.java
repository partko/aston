package com.example.usercrud.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO ошибки API.
 */
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> details;

    public ApiError() {
    }

    public ApiError(LocalDateTime timestamp, int status, String error, List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ApiError setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ApiError setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getError() {
        return error;
    }

    public ApiError setError(String error) {
        this.error = error;
        return this;
    }

    public List<String> getDetails() {
        return details;
    }

    public ApiError setDetails(List<String> details) {
        this.details = details;
        return this;
    }
}