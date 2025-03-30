package com.shopdevjava.springboot.application.port.in;

import com.shopdevjava.springboot.domain.model.User;

import java.util.List;

/**
 * Input port for User use cases
 */
public interface UserUseCase {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
} 