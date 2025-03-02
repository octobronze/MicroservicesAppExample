package com.example.gateway_service.auth.exceptions;

import com.example.gateway_service.auth.dtos.ExceptionResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Глобальный обработчик ошибок для WebFlux.
 */
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Обрабатывает исключения, возникающие в процессе обработки запроса.
     *
     * @param exchange текущий веб-обмен
     * @param ex       выброшенное исключение
     * @return {@link Mono}, который завершается, когда обработка ошибки завершена
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        var responseDto = new ExceptionResponseDto(ex.getMessage());
        HttpStatusCode status;

        if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
            log.error("BadRequestException occurred", ex);
        } else if (ex instanceof WebClientResponseException webClientEx) {
            status = webClientEx.getStatusCode();
            var responseBody = webClientEx.getResponseBodyAsString();
            if (!responseBody.isBlank()) {
                try {
                    var clientResponse = objectMapper.readValue(responseBody, ExceptionResponseDto.class);
                    responseDto = new ExceptionResponseDto(clientResponse.message());
                } catch (JsonProcessingException e) {
                    responseDto = new ExceptionResponseDto(webClientEx.getMessage());
                }
            } else {
                responseDto = new ExceptionResponseDto(webClientEx.getMessage());
            }
            log.error("WebClientResponseException occurred", ex);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            log.error("Exception occurred", ex);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer dataBuffer;
        try {
            var bytes = objectMapper.writeValueAsBytes(responseDto);
            dataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
        } catch (JsonProcessingException e) {
            log.error("Error writing response", e);
            var bytes = "{\"message\":\"Internal Server Error\"}".getBytes();
            dataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
        }

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
