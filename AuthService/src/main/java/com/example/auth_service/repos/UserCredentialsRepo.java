package com.example.auth_service.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth_service.tables.UserCredentials;

import java.util.Optional;

public interface UserCredentialsRepo extends JpaRepository<UserCredentials, Integer> {
    Optional<UserCredentials> findByEmail(String email);
}
