package com.example.gateway_service.auth.controllers;

import com.example.gateway_service.auth.dtos.LoginRequestDto;
import com.example.gateway_service.auth.dtos.LoginResponseDto;
import com.example.gateway_service.auth.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
