package com.example.email_service.exceptions;

/**
 * Исключение почтового сервиса.
 */
public class MailException extends RuntimeException {
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
