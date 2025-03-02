package com.example.gateway_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${kafka.email.topic.name}")
    private String emailTopicName;

    @Bean
    public NewTopic mailTopic() {
        return TopicBuilder.name(emailTopicName).build();
    }
}
