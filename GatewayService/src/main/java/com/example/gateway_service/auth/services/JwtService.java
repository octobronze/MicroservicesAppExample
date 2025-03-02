package com.example.gateway_service.auth.services;

import com.example.gateway_service.auth.other.JwtCheckResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Сервис для работы с JWT токенами.
 */
@Service
public class JwtService {
    private static final String JWT_EXPIRED = "Jwt has been expired";
    private static final String JWT_NOT_VALID = "Jwt not valid";

    @Value("${jwt.signing.key}")
    private String userTokenSigningKey;

    /**
     * Проверяет валидность токена пользователя.
     *
     * @param token JWT токен
     * @return {@link JwtCheckResponse} с результатом проверки
     */
    public JwtCheckResponse checkUserToken(String token) {
        return checkToken(token, userTokenSigningKey);
    }

    private JwtCheckResponse checkToken(String token, String signingKey) {
        try {
            var claims = getClaims(token, signingKey);
            return new JwtCheckResponse(true, "", claims);
        } catch (ExpiredJwtException expiredJwtException) {
            return new JwtCheckResponse(false, JWT_EXPIRED, null);
        } catch (SignatureException | MalformedJwtException signatureException) {
            return new JwtCheckResponse(false, JWT_NOT_VALID, null);
        }
    }

    private Claims getClaims(String token, String signingKey) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token).getPayload();
    }
}
