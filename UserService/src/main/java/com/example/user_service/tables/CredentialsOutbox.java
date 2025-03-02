package com.example.user_service.tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import com.example.user_service.enums.OutboxStatus;

/**
 * Сущность outbox для учетных данных пользователя.
 */
@Entity
@Table(name = "credentials_outbox")
@Getter
@Setter
public class CredentialsOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
