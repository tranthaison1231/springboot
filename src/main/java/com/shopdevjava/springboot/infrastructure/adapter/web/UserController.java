package com.shopdevjava.springboot.infrastructure.adapter.web;

import com.shopdevjava.springboot.application.port.in.UserUseCase;
import com.shopdevjava.springboot.domain.model.User;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.ApiResponse;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.UserRequest;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    @Autowired
    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userResponses = userUseCase.getAllUsers().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        User user = userUseCase.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", mapToResponse(user)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = mapToModel(userRequest);
        User createdUser = userUseCase.createUser(user);
        
        return new ResponseEntity<>(
            ApiResponse.success("User created successfully", mapToResponse(createdUser)),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRequest userRequest) {
        
        User user = mapToModel(userRequest);
        User updatedUser = userUseCase.updateUser(id, user);
        
        return ResponseEntity.ok(
            ApiResponse.success("User updated successfully", mapToResponse(updatedUser))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    /**
     * Maps a domain model to a response DTO
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    /**
     * Maps a request DTO to a domain model
     */
    private User mapToModel(UserRequest userRequest) {
        return new User(
            userRequest.getFirstName(),
            userRequest.getLastName(),
            userRequest.getEmail(),
            userRequest.getPassword()
        );
    }
} 