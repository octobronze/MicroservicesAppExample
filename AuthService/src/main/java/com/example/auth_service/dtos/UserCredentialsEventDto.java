package com.example.auth_service.dtos;

public record UserCredentialsEventDto(
        Integer id,
        String email,
        String password
) {
}
