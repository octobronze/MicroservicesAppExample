package com.example.gateway_service.auth.dtos;

public class LoginResponseDto {
    private String token;
    private Long duration;

    public LoginResponseDto() {

    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getDuration() {
        return duration;
    }

    public String getToken() {
        return token;
    }
}
