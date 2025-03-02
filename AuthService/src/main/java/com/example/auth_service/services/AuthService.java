package com.example.auth_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth_service.dtos.UserCredentialsEventDto;
import com.example.auth_service.dtos.LoginRequestDto;
import com.example.auth_service.dtos.LoginResponseDto;
import com.example.auth_service.exceptions.ValidationException;
import com.example.auth_service.repos.UserCredentialsRepo;

import com.example.auth_service.tables.UserCredentials;

import static com.example.auth_service.consts.ExceptionConsts.INVALID_DATA_EXCEPTION;

/**
 * Сервис для аутентификации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsRepo userCredentialsRepo;

    @Value("${jwt.duration}")
    private Long jwtDuration;

    /**
     * Авторизует пользователя.
     *
     * @param dto  данные для авторизации
     * @return объект {@link LoginResponseDto} с токеном
     */
    public LoginResponseDto login(LoginRequestDto dto) {
        var credentials = userCredentialsRepo.findByEmail(dto.email())
                .orElseThrow(() -> new ValidationException(INVALID_DATA_EXCEPTION));

        if (passwordEncoder.matches(dto.password(), credentials.getPassword())) {
            var token = jwtService.generateTokenForUser(credentials.getId());

            return new LoginResponseDto(token, jwtDuration);
        }

        throw new ValidationException(INVALID_DATA_EXCEPTION);
    }

    /**
     * Сохраняет учетные данные пользователя из Kafka.
     *
     * @param dto  учетные данные
     */
    public void saveCredentials(UserCredentialsEventDto dto) {
        var credentials = new UserCredentials();
        credentials.setId(dto.id());
        credentials.setEmail(dto.email());
        credentials.setPassword(dto.password());
        userCredentialsRepo.save(credentials);
    }
}
