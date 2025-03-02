package com.example.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Включает планирование отправки outbox во всех процессах, кроме API.
 */
@Profile("worker")
@Configuration
@EnableScheduling
public class OutboxSchedulerConfig {
}
