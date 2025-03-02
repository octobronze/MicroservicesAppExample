package com.example.user_service.dtos;

public record UserProfileResponseDto(
        String email,
        String firstName,
        String lastName
) {
}
