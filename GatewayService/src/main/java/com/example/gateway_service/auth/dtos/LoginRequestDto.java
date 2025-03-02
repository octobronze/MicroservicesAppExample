package com.example.gateway_service.auth.dtos;

public class LoginRequestDto {
    private String email;
    private String password;

    public LoginRequestDto() {

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
