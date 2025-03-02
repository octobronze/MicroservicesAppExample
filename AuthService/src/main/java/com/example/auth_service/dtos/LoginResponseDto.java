package com.example.auth_service.dtos;

public record LoginResponseDto(
        String token,
        Long duration
) {
}
