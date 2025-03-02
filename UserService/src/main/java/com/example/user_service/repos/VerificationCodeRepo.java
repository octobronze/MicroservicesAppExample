package com.example.user_service.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.tables.VerificationCode;

import java.util.Optional;

/**
 * Репозиторий для работы с кодами верификации.
 */
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Integer> {
    Optional<VerificationCode> findByEmail(String email);
}
