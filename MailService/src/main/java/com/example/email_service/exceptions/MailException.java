package com.example.email_service.exceptions;

public class MailException extends RuntimeException {
    public MailException(String message) {
        super(message);
    }
}
