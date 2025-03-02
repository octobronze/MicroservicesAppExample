package com.example.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.example.auth_service.dtos.ExceptionResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    /**
     * Обрабатывает исключение {@link ValidationException}.
     *
     * @param exception  исключение
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationException(ValidationException exception) {
        var response = new ExceptionResponseDto(exception.getMessage());
        log.error("Bad request exception occurred", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param exception  исключение
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception) {
        var response = new ExceptionResponseDto(exception.getMessage());
        log.error("Unexpected exception occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Обрабатывает исключение {@link HttpClientErrorException}.
     *
     * @param exception  исключение
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ExceptionResponseDto> handleHttpClientErrorException(HttpClientErrorException exception) {
        var body = exception.getResponseBodyAs(ExceptionResponseDto.class);
        var message = body != null ? body.message() : exception.getMessage();
        var response = new ExceptionResponseDto(message);
        log.error("HTTP client error exception occurred", exception);
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
