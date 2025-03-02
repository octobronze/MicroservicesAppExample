package com.example.user_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.user_service.dtos.UserCredentialsEventDto;
import com.example.user_service.dtos.VerificationDto;

/**
 * Продюсер Kafka для публикации событий пользователей.
 */
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.email.topic.name}")
    private String emailTopicName;

    @Value("${kafka.credentials.topic.name}")
    private String credentialsTopicName;

    /**
     * Отправляет событие верификации в топик почты.
     *
     * @param dto  данные для отправки письма
     */
    public void sendToEmailTopic(VerificationDto dto) throws JsonProcessingException {
        var jsonString = objectMapper.writeValueAsString(dto);

        send(emailTopicName, jsonString);
    }

    /**
     * Отправляет событие учетных данных в топик credentials.
     *
     * @param dto  учетные данные пользователя
     */
    public void sendToCredentialsTopic(UserCredentialsEventDto dto) throws JsonProcessingException {
        var jsonString = objectMapper.writeValueAsString(dto);

        send(credentialsTopicName, jsonString);
    }

    private void send(String topicName, String message) {
        kafkaTemplate.send(topicName, message);
    }
}
