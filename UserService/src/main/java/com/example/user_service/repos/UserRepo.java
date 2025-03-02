package com.example.user_service.repos;

import com.example.user_service.tables.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByEmailAndIsRegisteredTrue(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsRegisteredTrue(String email);
    Optional<User> findByIdAndIsRegisteredTrue(Integer id);
}
