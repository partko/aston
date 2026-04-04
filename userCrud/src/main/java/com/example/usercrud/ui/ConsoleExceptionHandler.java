package com.example.usercrud.ui;

import com.example.usercrud.exception.DataAccessException;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.exception.UniqueConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Централизованный обработчик исключений для консольного приложения.
 */
public final class ConsoleExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleExceptionHandler.class);

    private ConsoleExceptionHandler() {
    }

    /**
     * Выполняет действие пользователя и обрабатывает ожидаемые исключения.
     *
     * @param action действие пользователя
     */
    public static void handle(Runnable action) {
        try {
            action.run();

        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка валидации или ввода: {}", e.getMessage());
            System.out.println("Ошибка ввода: " + e.getMessage());

        } catch (NotFoundException e) {
            logger.warn("Сущность не найдена: {}", e.getMessage());
            System.out.println("Сущность не найдена: " + e.getMessage());

        } catch (UniqueConstraintViolationException e) {
            logger.warn("Нарушение уникального ограничения: constraint={}", e.getConstraintName());
            System.out.println("Ошибка: пользователь с таким email уже существует.");

        } catch (DataAccessException e) {
            logger.error("Ошибка доступа к данным", e);
            System.out.println("Ошибка доступа к данным: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Непредвиденная системная ошибка", e);
            System.out.println("Непредвиденная системная ошибка: " + e.getMessage());
        }
    }
}