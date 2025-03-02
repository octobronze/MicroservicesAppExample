package com.example.user_service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dtos.UserInfoResponseDto;
import com.example.user_service.dtos.UserProfileResponseDto;
import com.example.user_service.dtos.UserRegistrationRequestDto;
import com.example.user_service.services.UserService;

/**
 * Контроллер для работы с пользователями.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Регистрирует пользователя.
     *
     * @param dto  данные для регистрации
     * @return ответ об успешной регистрации
     */
    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequestDto dto) throws JsonProcessingException {
        userService.registerUser(dto);

        return ResponseEntity.ok("Ok");
    }

    /**
     * Подтверждает регистрацию пользователя по коду верификации.
     *
     * @param email  email пользователя
     * @param code  код верификации
     * @return ответ об успешной верификации
     */
    @GetMapping("/registration/verify/{email}/{code}")
    public ResponseEntity<String> verifyRegistration(
            @PathVariable(name = "email") String email,
            @PathVariable(name = "code") String code
    ) {
        userService.verifyUserRegistration(email, code);

        return ResponseEntity.ok("Ok");
    }

    /**
     * Возвращает информацию о пользователе.
     *
     * @param email  email пользователя
     * @return информация о пользователе
     */
    @GetMapping("/{email}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(userService.getUserInfo(email));
    }

    /**
     * Возвращает профиль пользователя.
     *
     * @param userId  идентификатор пользователя
     * @return профиль пользователя
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestHeader(name = "userId") Integer userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
