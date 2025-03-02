package com.example.user_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.repos.VerificationCodeRepo;
import com.example.user_service.tables.VerificationCode;

import java.security.SecureRandom;

/**
 * Сервис для работы с кодами верификации.
 */
@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private static final int MIN_CODE_VALUE = 1000;
    private static final int MAX_CODE_VALUE = 10000;

    private final VerificationCodeRepo verificationCodeRepo;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Генерирует код верификации для пользователя.
     *
     * @param email  email пользователя
     * @return сгенерированный код
     */
    @Transactional
    public String generateCodeForUser(String email) {
        var code = String.valueOf(secureRandom.nextInt(MIN_CODE_VALUE, MAX_CODE_VALUE));

        var verificationCode = verificationCodeRepo.findByEmail(email).orElseGet(VerificationCode::new);
        verificationCode.setEmail(email);
        verificationCode.setCode(code);

        verificationCodeRepo.save(verificationCode);

        return code;
    }

    /**
     * Проверяет код верификации пользователя.
     *
     * @param email  email пользователя
     * @param code  код верификации
     * @return true, если код совпадает, иначе false
     */
    @Transactional
    public boolean checkCodeForUser(String email, String code) {
        var verificationCodeOpt = verificationCodeRepo.findByEmail(email);

        return verificationCodeOpt.map(verificationCode -> verificationCode.getCode().equals(code)).orElse(false);
    }
}
