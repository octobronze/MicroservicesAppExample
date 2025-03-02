package com.example.gateway_service.auth.services;

import com.example.gateway_service.auth.other.UserDetails;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для получения данных пользователя.
 */
@Service
public class UserDetailsService {
    /**
     * Возвращает данные пользователя из {@link Claims} токена.
     *
     * @param claims данные из JWT токена
     * @return {@link UserDetails} с информацией о пользователе
     */
    @SuppressWarnings("unchecked")
    public UserDetails getUserDetailsFromClaims(Claims claims) {
        var email = (String) claims.get("email");
        var authorities = (List<String>) claims.get("authorities");

        return new UserDetails(email, authorities);
    }
}
