package com.example.user_service.exceptions;

/**
 * Исключение, выбрасываемое при неверном запросе.
 */
public class ValidationException extends RuntimeException {
    /**
     * Создает исключение с сообщением об ошибке.
     *
     * @param message  сообщение об ошибке
     */
    public ValidationException(String message) {
        super(message);
    }
}
