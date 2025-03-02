package com.example.gateway_service.auth.exceptions;

import com.example.gateway_service.auth.dtos.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadRequestException(BadRequestException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    ResponseEntity<ExceptionResponseDto> handleHttpClientErrorException(HttpClientErrorException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        String message = exception.getResponseBodyAs(ExceptionResponseDto.class).getMessage();

        response.setMessage(message);

        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
