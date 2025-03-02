package com.example.auth_service.kafka;

import com.example.auth_service.dtos.UserCredentialsEventDto;
import com.example.auth_service.services.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Консьюмер Kafka для AuthService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    /**
     * Читает событие создания учетных данных и сохраняет их.
     *
     * @param message  json сообщение
     */
    @KafkaListener(
            topics = "${kafka.credentials.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"auto.offset.reset=earliest"}
    )
    public void listenCredentials(String message) {
        try {
            var eventDto = objectMapper.readValue(message, UserCredentialsEventDto.class);
            authService.saveCredentials(eventDto);
        } catch (JsonProcessingException e) {
            log.error("Json error has occurred while parsing user credentials", e);
        }
    }
}
