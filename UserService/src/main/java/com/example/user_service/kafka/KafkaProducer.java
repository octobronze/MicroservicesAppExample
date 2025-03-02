package com.example.user_service.kafka;

import com.example.user_service.dtos.EmailSendDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.email.topic.name}")
    private String emailTopicName;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private void send(String topicName, String message) {
        kafkaTemplate.send(topicName, message);
    }

    public void sendToEmailTopic(EmailSendDto dto) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(dto);

        send(emailTopicName, jsonString);
    }
}
