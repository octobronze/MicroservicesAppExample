package com.example.user_service.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.tables.User;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByEmailAndRegisteredTrue(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRegisteredTrue(String email);
    Optional<User> findByIdAndRegisteredTrue(Integer id);
}
