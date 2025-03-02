package com.example.gateway_service.auth.services;

import com.example.gateway_service.auth.other.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsService {
    private final JwtService jwtService;

    public UserDetailsService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public UserDetails getUserDetailsFromJWT(String jwt) {
        String email = jwtService.getEmail(jwt);
        List<String> authorities = jwtService.getAuthorities(jwt);

        return new UserDetails(email, authorities);
    }
}
