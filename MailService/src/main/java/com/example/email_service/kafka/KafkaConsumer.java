package com.example.email_service.kafka;

import com.example.email_service.dtos.VerificationDto;
import com.example.email_service.services.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer для сообщений Kafka.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.mail.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listener(String data) throws JsonProcessingException {
        var dto = objectMapper.readValue(data, VerificationDto.class);
        mailService.sendVerificationLink(dto.email(), dto.verificationLink());
    }
}
