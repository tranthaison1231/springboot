package com.shopdevjava.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopdevjava.springboot.config.TestConfig;
import com.shopdevjava.springboot.domain.repository.UserRepository;
import com.shopdevjava.springboot.infrastructure.adapter.web.dto.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up database before each test
        userRepository.deleteAll();
        
        // Create a test user for tests that need an existing user
        UserRequest userRequest = new UserRequest("John", "Doe", "john.doe@example.com", "password123");
        
        MvcResult result = mockMvc.perform(post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Extract the user ID from the response
        String response = result.getResponse().getContentAsString();
        testUserId = objectMapper.readTree(response)
                .path("data")
                .path("id")
                .asLong();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /v1/api/users - Get All Users")
    void getAllUsers_ReturnsUsers() throws Exception {
        mockMvc.perform(get("/v1/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("GET /v1/api/users/{id} - Get User By ID")
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        mockMvc.perform(get("/v1/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("GET /v1/api/users/{id} - Get User By ID with non-existent ID")
    void getUserById_NonExistentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/v1/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    @DisplayName("POST /v1/api/users - Create New User")
    void createUser_ValidRequest_ReturnsCreatedUser() throws Exception {
        UserRequest newUser = new UserRequest("Jane", "Smith", "jane.smith@example.com", "password456");
        
        mockMvc.perform(post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"));
        
        // Verify the total count of users is now 2
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("POST /v1/api/users - Create User with Duplicate Email")
    void createUser_DuplicateEmail_ReturnsBadRequest() throws Exception {
        UserRequest duplicateUser = new UserRequest("Another", "User", "john.doe@example.com", "password789");
        
        mockMvc.perform(post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already in use"));
        
        // Verify no new user was added
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("PUT /v1/api/users/{id} - Update User")
    void updateUser_ValidUpdate_ReturnsUpdatedUser() throws Exception {
        UserRequest updateRequest = new UserRequest("John", "Updated", "john.updated@example.com", "newpassword");
        
        mockMvc.perform(put("/v1/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Updated"))
                .andExpect(jsonPath("$.data.email").value("john.updated@example.com"));
        
        // Verify the user was updated in the database
        assertTrue(userRepository.findByEmail("john.updated@example.com").isPresent());
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("DELETE /v1/api/users/{id} - Delete User")
    void deleteUser_ExistingUser_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/v1/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
        
        // Verify the user was deleted
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("POST /v1/api/users - Validation Error")
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UserRequest invalidUser = new UserRequest("", "", "invalid-email", "pwd");
        
        mockMvc.perform(post("/v1/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
        
        // Verify no new user was added
        assertEquals(1, userRepository.count());
    }
} 