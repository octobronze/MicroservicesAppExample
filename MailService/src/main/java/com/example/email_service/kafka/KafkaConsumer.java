package com.example.email_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final KafkaService kafkaService;

    public KafkaConsumer(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @KafkaListener(topics = "Mail-topic", groupId = "group1")
    public void listener(String data) throws JsonProcessingException {
        kafkaService.produce(data);
    }
}
