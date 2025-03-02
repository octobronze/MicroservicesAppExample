package com.example.user_service.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.enums.OutboxStatus;
import com.example.user_service.tables.EmailOutbox;

import java.util.List;

/**
 * Репозиторий для работы с email outbox.
 */
public interface EmailOutboxRepo extends JpaRepository<EmailOutbox, Integer> {
    List<EmailOutbox> findAllByStatus(OutboxStatus status);
}
