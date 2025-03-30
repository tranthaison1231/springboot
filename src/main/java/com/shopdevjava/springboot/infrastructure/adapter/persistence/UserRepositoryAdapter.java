package com.shopdevjava.springboot.infrastructure.adapter.persistence;

import com.shopdevjava.springboot.domain.model.User;
import com.shopdevjava.springboot.domain.repository.UserRepository;
import com.shopdevjava.springboot.infrastructure.adapter.persistence.entity.UserEntity;
import com.shopdevjava.springboot.infrastructure.adapter.persistence.repository.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter for User repository that implements the domain repository interface
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Autowired
    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(this::mapToDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::mapToDomainModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::mapToDomainModel);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = mapToEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return mapToDomainModel(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void deleteAll() {
        jpaUserRepository.deleteAll();
    }
    
    @Override
    public long count() {
        return jpaUserRepository.count();
    }

    /**
     * Maps a JPA entity to a domain model
     */
    private User mapToDomainModel(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setEmail(userEntity.getEmail());
        user.setPassword(userEntity.getPassword());
        user.setCreatedAt(userEntity.getCreatedAt());
        user.setUpdatedAt(userEntity.getUpdatedAt());
        return user;
    }

    /**
     * Maps a domain model to a JPA entity
     */
    private UserEntity mapToEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        
        if (user.getCreatedAt() != null) {
            userEntity.setCreatedAt(user.getCreatedAt());
        }
        
        if (user.getUpdatedAt() != null) {
            userEntity.setUpdatedAt(user.getUpdatedAt());
        }
        
        return userEntity;
    }
} 