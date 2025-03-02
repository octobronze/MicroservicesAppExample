package com.example.gateway_service.auth.repos;

import com.example.gateway_service.auth.tables.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepo extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findByValue(String value);
}
