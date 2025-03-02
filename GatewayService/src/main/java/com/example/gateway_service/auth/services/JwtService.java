package com.example.gateway_service.auth.services;

import com.example.gateway_service.auth.other.JwtCheckResponse;
import com.example.gateway_service.auth.repos.JwtRepo;
import com.example.gateway_service.auth.tables.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private static final String JWT_EXPIRED = "Jwt has been expired";
    private static final String JWT_NOT_EXISTS = "Jwt not exist";
    private static final String JWT_NOT_VALID = "Jwt not valid";

    private final JwtRepo jwtRepo;

    @Value("${jwt.signing.key}")
    private String userTokenSigningKey;

    @Value("${jwt.duration}")
    private Long jwtDuration;

    public JwtService(JwtRepo jwtRepo) {
        this.jwtRepo = jwtRepo;
    }

    public JwtCheckResponse checkUserToken(String token) {
        return checkToken(token, userTokenSigningKey);
    }

    private JwtCheckResponse checkToken(String token, String signingKey) {
        try {
            getClaims(token, signingKey);

            Jwt jwt = jwtRepo.findByValue(token).orElse(null);

            if (jwt == null) {
                return new JwtCheckResponse(false, JWT_NOT_EXISTS);
            }
            if (!jwt.isValid()) {
                return new JwtCheckResponse(false, JWT_NOT_VALID);
            }
        } catch (ExpiredJwtException expiredJwtException) {
            return new JwtCheckResponse(false, JWT_EXPIRED);
        } catch (SignatureException | MalformedJwtException signatureException) {
            return new JwtCheckResponse(false, JWT_NOT_VALID);
        }

        return new JwtCheckResponse(true, "");
    }

    private Claims getClaims(String token, String signingKey) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token).getPayload();
    }

    public Integer getUserId(String token) {
        Claims claims = getClaims(token, userTokenSigningKey);

        return Integer.parseInt(claims.getSubject());
    }

    public String getEmail(String token) {
        Claims claims = getClaims(token, userTokenSigningKey);

        return (String) claims.get("email");
    }

    public List<String> getAuthorities(String token) {
        Claims claims = getClaims(token, userTokenSigningKey);

        return (List<String>) claims.get("authorities");
    }

    public String generateTokenForUser(Integer userId) {
        return generateToken(new HashMap<>(), userId.toString(),
                Keys.hmacShaKeyFor(userTokenSigningKey.getBytes(StandardCharsets.UTF_8)));
    }

    private String generateToken(Map<String, Object> claims, String subject, Key signingKey) {
        String token = Jwts.builder().subject(subject).claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtDuration))
                .signWith(signingKey).compact();

        Jwt jwt = new Jwt();
        jwt.setValid(true);
        jwt.setValue(token);

        jwtRepo.save(jwt);

        return token;
    }
}
