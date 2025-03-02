package com.example.user_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.dtos.UserCredentialsEventDto;
import com.example.user_service.dtos.VerificationDto;
import com.example.user_service.enums.OutboxStatus;
import com.example.user_service.kafka.KafkaProducer;
import com.example.user_service.repos.CredentialsOutboxRepo;
import com.example.user_service.repos.EmailOutboxRepo;

/**
 * Планировщик отправки событий из outbox в Kafka.
 */
@Profile("worker")
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {
    private static final long OUTBOX_POLL_DELAY_MS = 5000L;

    private final EmailOutboxRepo emailOutboxRepo;
    private final CredentialsOutboxRepo credentialsOutboxRepo;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    /**
     * Отправляет отложенные события из outbox в Kafka.
     */
    @Scheduled(fixedDelay = OUTBOX_POLL_DELAY_MS)
    @Transactional
    public void processOutbox() {
        var emailEvents = emailOutboxRepo.findAllByStatus(OutboxStatus.PENDING);
        for (var event : emailEvents) {
            try {
                var dto = objectMapper.readValue(event.getPayload(), VerificationDto.class);
                kafkaProducer.sendToEmailTopic(dto);
                emailOutboxRepo.delete(event);
            } catch (Exception e) {
                log.error("Failed to process email outbox event id {}", event.getId(), e);
                event.setStatus(OutboxStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                emailOutboxRepo.save(event);
            }
        }

        var credentialsEvents = credentialsOutboxRepo.findAllByStatus(OutboxStatus.PENDING);
        for (var event : credentialsEvents) {
            try {
                var dto = objectMapper.readValue(event.getPayload(), UserCredentialsEventDto.class);
                kafkaProducer.sendToCredentialsTopic(dto);
                credentialsOutboxRepo.delete(event);
            } catch (Exception e) {
                log.error("Failed to process credentials outbox event id {}", event.getId(), e);
                event.setStatus(OutboxStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                credentialsOutboxRepo.save(event);
            }
        }
    }
}
