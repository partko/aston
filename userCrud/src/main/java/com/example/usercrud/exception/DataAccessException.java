package com.example.usercrud.exception;

/**
 * Исключение, возникающее при ошибках доступа к данным.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
