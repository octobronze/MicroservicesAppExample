package com.example.gateway_service.auth.dtos;

public class UserResponseDto {
    private Integer id;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }
}
