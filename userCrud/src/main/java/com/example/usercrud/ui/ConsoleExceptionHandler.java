package com.example.usercrud.ui;

import com.example.usercrud.exception.DataAccessException;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.exception.UniqueConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConsoleExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleExceptionHandler.class);

    private ConsoleExceptionHandler() {
    }

    public static void handle(Runnable action) {
        try {
            action.run();

        } catch (IllegalArgumentException e) {
            logger.warn("Validation/input error: {}", e.getMessage());
            System.out.println("Input error: " + e.getMessage());

        } catch (NotFoundException e) {
            logger.warn("Entity not found: {}", e.getMessage());
            System.out.println("Not found: " + e.getMessage());

        } catch (UniqueConstraintViolationException e) {
            logger.warn("Unique constraint violation: constraint={}", e.getConstraintName());
            System.out.println("Error: user with this email already exists.");

        } catch (DataAccessException e) {
            logger.error("Database access error", e);
            System.out.println("Database error: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Unexpected system error", e);
            System.out.println("System error: " + e.getMessage());
        }
    }
}