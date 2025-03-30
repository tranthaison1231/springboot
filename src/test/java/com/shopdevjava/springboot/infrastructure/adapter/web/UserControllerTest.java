package com.shopdevjava.springboot.infrastructure.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopdevjava.springboot.application.port.in.UserUseCase;
import com.shopdevjava.springboot.domain.model.User;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserUseCase userUseCase;

    private User testUser1;
    private User testUser2;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User("John", "Doe", "john.doe@example.com", "password123");
        testUser1.setId(1L);
        testUser1.setCreatedAt(LocalDateTime.now());
        testUser1.setUpdatedAt(LocalDateTime.now());

        testUser2 = new User("Jane", "Smith", "jane.smith@example.com", "password456");
        testUser2.setId(2L);
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());

        // Create user request for POST/PUT tests
        userRequest = new UserRequest("New", "User", "new.user@example.com", "newpassword123");
    }

    @Test
    @DisplayName("GET /v1/api/users - Get All Users")
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userUseCase.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/v1/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(testUser1.getId()))
                .andExpect(jsonPath("$.data[0].firstName").value(testUser1.getFirstName()))
                .andExpect(jsonPath("$.data[0].email").value(testUser1.getEmail()))
                .andExpect(jsonPath("$.data[1].id").value(testUser2.getId()))
                .andExpect(jsonPath("$.data[1].firstName").value(testUser2.getFirstName()))
                .andExpect(jsonPath("$.data[1].email").value(testUser2.getEmail()));

        verify(userUseCase).getAllUsers();
    }

    @Test
    @DisplayName("GET /v1/api/users/{id} - Get User By ID")
    void getUserById_ReturnsUser() throws Exception {
        // Arrange
        when(userUseCase.getUserById(1L)).thenReturn(testUser1);

        // Act & Assert
        mockMvc.perform(get("/v1/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(testUser1.getId()))
                .andExpect(jsonPath("$.data.firstName").value(testUser1.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(testUser1.getLastName()))
                .andExpect(jsonPath("$.data.email").value(testUser1.getEmail()));

        verify(userUseCase).getUserById(1L);
    }

    @Test
    @DisplayName("POST /v1/api/users - Create User")
    void createUser_ReturnsCreatedUser() throws Exception {
        // Arrange
        User newUser = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                userRequest.getPassword()
        );
        User savedUser = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                userRequest.getPassword()
        );
        savedUser.setId(3L);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userUseCase.createUser(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/v1/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.firstName").value(savedUser.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(savedUser.getLastName()))
                .andExpect(jsonPath("$.data.email").value(savedUser.getEmail()));

        verify(userUseCase).createUser(any(User.class));
    }

    @Test
    @DisplayName("PUT /v1/api/users/{id} - Update User")
    void updateUser_ReturnsUpdatedUser() throws Exception {
        // Arrange
        User updatedUser = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                userRequest.getPassword()
        );
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(testUser1.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userUseCase.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/v1/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.data.firstName").value(updatedUser.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(updatedUser.getLastName()))
                .andExpect(jsonPath("$.data.email").value(updatedUser.getEmail()));

        verify(userUseCase).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("DELETE /v1/api/users/{id} - Delete User")
    void deleteUser_ReturnsSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(userUseCase).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/v1/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userUseCase).deleteUser(1L);
    }

    @Test
    @DisplayName("POST /v1/api/users - Validation Error")
    void createUser_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange - create an invalid user request (missing required fields)
        UserRequest invalidRequest = new UserRequest("", "", "invalid-email", "pwd");

        // Act & Assert
        mockMvc.perform(post("/v1/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userUseCase, never()).createUser(any(User.class));
    }
} 