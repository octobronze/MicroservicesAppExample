package com.example.auth_service.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с JWT токенами.
 */
@Service
public class JwtService {
    @Value("${jwt.signing.key}")
    private String userTokenSigningKey;

    @Value("${jwt.duration}")
    private Long jwtDuration;

    private Key cachedSigningKey;

    @PostConstruct
    public void init() {
        this.cachedSigningKey = Keys.hmacShaKeyFor(userTokenSigningKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерирует токен для пользователя.
     *
     * @param userId  идентификатор пользователя
     * @return сгенерированный токен
     */
    public String generateTokenForUser(Integer userId) {
        return generateToken(new HashMap<>(), userId.toString(), cachedSigningKey);
    }

    private String generateToken(Map<String, Object> claims, String subject, Key signingKey) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtDuration))
                .signWith(signingKey)
                .compact();
    }
}
