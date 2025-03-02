package com.example.auth_service.dtos;

public record LoginRequestDto(
        String email,
        String password
) {
}
