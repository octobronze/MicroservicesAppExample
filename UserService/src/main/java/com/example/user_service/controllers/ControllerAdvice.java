package com.example.user_service.controllers;

import com.example.user_service.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.user_service.dtos.ExceptionResponseDto;

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
        log.error("Bad request exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения валидации аргументов метода.
     * <p>
     * Отлавливает исключения выбрасываемые в случае ошибок валидации полей, которые используют аннотации из {@link jakarta.validation.constraints}.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var response = new ExceptionResponseDto(extractDefaultMessage(exception));
        log.error("Bad request exception: {}", exception.getMessage(), exception);

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
        log.error("Internal server error", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponseDto("Internal Server Error"));
    }

    private String extractDefaultMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldError().getDefaultMessage();
    }
}
