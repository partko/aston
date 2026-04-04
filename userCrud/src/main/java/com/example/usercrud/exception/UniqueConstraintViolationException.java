package com.example.usercrud.exception;

/**
 * Исключение, возникающее при нарушении уникального ограничения в базе данных.
 */
public class UniqueConstraintViolationException extends RuntimeException {
    private final String constraintName;

    public UniqueConstraintViolationException(String message, String constraintName, Throwable cause) {
        super(message, cause);
        this.constraintName = constraintName;
    }

    /**
     * Возвращает имя ограничения базы данных, которое было нарушено.
     *
     * @return имя ограничения
     */
    public String getConstraintName() {
        return constraintName;
    }
}
