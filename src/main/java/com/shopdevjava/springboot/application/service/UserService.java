package com.shopdevjava.springboot.application.service;

import com.shopdevjava.springboot.application.port.in.UserUseCase;
import com.shopdevjava.springboot.domain.exception.UserException;
import com.shopdevjava.springboot.domain.model.User;
import com.shopdevjava.springboot.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for User use cases
 */
@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with id: " + id, 
                        UserException.UserExceptionType.NOT_FOUND));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email, 
                        UserException.UserExceptionType.NOT_FOUND));
    }

    @Override
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("Email already in use", 
                    UserException.UserExceptionType.DUPLICATE_EMAIL);
        }
        
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        // Check if new email already exists for another user
        if (!existingUser.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("Email already in use", 
                    UserException.UserExceptionType.DUPLICATE_EMAIL);
        }
        
        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        
        // Only update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException("User not found with id: " + id, 
                    UserException.UserExceptionType.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
} 