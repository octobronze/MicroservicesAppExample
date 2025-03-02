package com.example.user_service.dtos;

public record UserCredentialsEventDto(
        Integer id,
        String email,
        String password
) {
}
