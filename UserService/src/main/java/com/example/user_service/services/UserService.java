package com.example.user_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.dtos.UserCredentialsEventDto;
import com.example.user_service.dtos.UserProfileResponseDto;
import com.example.user_service.dtos.UserRegistrationRequestDto;
import com.example.user_service.dtos.VerificationDto;
import com.example.user_service.exceptions.ValidationException;
import com.example.user_service.repos.CredentialsOutboxRepo;
import com.example.user_service.repos.EmailOutboxRepo;
import com.example.user_service.repos.UserRepo;
import com.example.user_service.tables.CredentialsOutbox;
import com.example.user_service.tables.EmailOutbox;
import com.example.user_service.tables.User;

import static com.example.user_service.consts.ExceptionConsts.USER_NOT_FOUND;
import static com.example.user_service.consts.ExceptionConsts.USER_WITH_EMAIL_EXISTS;
import static com.example.user_service.consts.ExceptionConsts.VERIFICATION_ERROR;

/**
 * Сервис для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailOutboxRepo emailOutboxRepo;
    private final CredentialsOutboxRepo credentialsOutboxRepo;
    private final ObjectMapper objectMapper;
    private final VerificationCodeService verificationCodeService;

    @Value("${verification.url}")
    private String verificationUrl;

    /**
     * Регистрирует пользователя и ставит связанные события в outbox.
     *
     * @param dto  данные для регистрации
     */
    @Transactional
    public void registerUser(UserRegistrationRequestDto dto) throws JsonProcessingException {
        if (userRepo.existsByEmailAndRegisteredTrue(dto.email())) {
            throw new ValidationException(USER_WITH_EMAIL_EXISTS);
        }
        var encryptedPassword = passwordEncoder.encode(dto.password());

        var user = userRepo.findByEmail(dto.email()).orElseGet(() -> {
            var newUser = new User();

            newUser.setEmail(dto.email());
            newUser.setRegistered(false);
            newUser.setFirstName(dto.firstName());
            newUser.setPassword(encryptedPassword);
            newUser.setLastName(dto.lastName());

            return newUser;
        });

        try {
            userRepo.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException(USER_WITH_EMAIL_EXISTS);
        }

        var credentialsEvent = new UserCredentialsEventDto(user.getId(), user.getEmail(), user.getPassword());
        var credentialsOutbox = new CredentialsOutbox();
        credentialsOutbox.setPayload(objectMapper.writeValueAsString(credentialsEvent));
        credentialsOutboxRepo.save(credentialsOutbox);

        var code = verificationCodeService.generateCodeForUser(dto.email());
        var verificationLink = generateVerificationLink(dto.email(), code);

        var sendDto = new VerificationDto(dto.email(), verificationLink);

        var outbox = new EmailOutbox();
        outbox.setPayload(objectMapper.writeValueAsString(sendDto));
        emailOutboxRepo.save(outbox);
    }

    /**
     * Подтверждает регистрацию пользователя по коду верификации.
     *
     * @param email  email пользователя
     * @param code  код верификации
     */
    @Transactional
    public void verifyUserRegistration(String email, String code) {
        boolean isValid = verificationCodeService.checkCodeForUser(email, code);

        if (isValid) {
            var user = userRepo.findByEmail(email).orElseThrow(() -> new ValidationException(USER_NOT_FOUND));
            user.setRegistered(true);
            userRepo.save(user);

            return;
        }

        throw new ValidationException(VERIFICATION_ERROR);
    }

    /**
     * Возвращает профиль пользователя.
     *
     * @param userId  идентификатор пользователя
     * @return объект {@link UserProfileResponseDto} с данными профиля
     */
    public UserProfileResponseDto getUserProfile(Integer userId) {
        var user = userRepo.findByIdAndRegisteredTrue(userId).orElseThrow(() -> new ValidationException(USER_NOT_FOUND));

        return new UserProfileResponseDto(user.getEmail(), user.getFirstName(), user.getLastName());
    }

    private String generateVerificationLink(String email, String code) {
        return verificationUrl + "/user/registration/verify/" + email + "/" + code;
    }
}
