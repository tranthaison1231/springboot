package com.shopdevjava.springboot.domain.repository;

import com.shopdevjava.springboot.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User domain model
 */
public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    void deleteAll();
    long count();
} 