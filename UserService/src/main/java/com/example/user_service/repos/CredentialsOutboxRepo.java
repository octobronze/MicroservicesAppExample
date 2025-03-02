package com.example.user_service.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.enums.OutboxStatus;
import com.example.user_service.tables.CredentialsOutbox;

import java.util.List;

/**
 * Репозиторий для работы с credentials outbox.
 */
public interface CredentialsOutboxRepo extends JpaRepository<CredentialsOutbox, Integer> {
    List<CredentialsOutbox> findAllByStatus(OutboxStatus status);
}
