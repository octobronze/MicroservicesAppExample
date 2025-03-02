package com.example.user_service.dtos;

public record UserInfoResponseDto(
        Integer id,
        String email,
        String password
) {
}
