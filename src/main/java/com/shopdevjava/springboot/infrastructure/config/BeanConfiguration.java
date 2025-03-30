package com.shopdevjava.springboot.infrastructure.config;

import com.shopdevjava.springboot.application.port.in.UserUseCase;
import com.shopdevjava.springboot.application.service.UserService;
import com.shopdevjava.springboot.domain.repository.UserRepository;
import com.shopdevjava.springboot.infrastructure.adapter.persistence.UserRepositoryAdapter;
import com.shopdevjava.springboot.infrastructure.adapter.persistence.repository.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for dependency injection
 */
@Configuration
public class BeanConfiguration {

    /**
     * Creates a UserRepository bean that adapts the JPA repository
     */
    @Bean
    public UserRepository userRepository(JpaUserRepository jpaUserRepository) {
        return new UserRepositoryAdapter(jpaUserRepository);
    }
    
    /**
     * Creates a UserUseCase bean that implements the application service
     */
    @Bean
    public UserUseCase userUseCase(UserRepository userRepository) {
        return new UserService(userRepository);
    }
} 