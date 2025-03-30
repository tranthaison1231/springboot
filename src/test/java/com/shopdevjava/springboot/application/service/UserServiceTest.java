package com.shopdevjava.springboot.application.service;

import com.shopdevjava.springboot.domain.exception.UserException;
import com.shopdevjava.springboot.domain.model.User;
import com.shopdevjava.springboot.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Set up test users
        testUser1 = new User("John", "Doe", "john.doe@example.com", "password123");
        testUser1.setId(1L);
        testUser1.setCreatedAt(LocalDateTime.now());
        testUser1.setUpdatedAt(LocalDateTime.now());

        testUser2 = new User("Jane", "Smith", "jane.smith@example.com", "password456");
        testUser2.setId(2L);
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Get All Users - Returns list of users")
    void getAllUsers_ReturnsListOfUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get User By ID - Returns user when found")
    void getUserById_UserExists_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser1.getId(), result.getId());
        assertEquals(testUser1.getFirstName(), result.getFirstName());
        assertEquals(testUser1.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Get User By ID - Throws exception when user not found")
    void getUserById_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.getUserById(999L));
        assertEquals(UserException.UserExceptionType.NOT_FOUND, exception.getType());
        assertTrue(exception.getMessage().contains("not found"));
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Get User By Email - Returns user when found")
    void getUserByEmail_UserExists_ReturnsUser() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        // Act
        User result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(testUser1.getId(), result.getId());
        assertEquals(testUser1.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Get User By Email - Throws exception when user not found")
    void getUserByEmail_UserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.getUserByEmail(email));
        assertEquals(UserException.UserExceptionType.NOT_FOUND, exception.getType());
        assertTrue(exception.getMessage().contains("not found"));
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Create User - Returns created user")
    void createUser_ValidUser_ReturnsCreatedUser() {
        // Arrange
        User newUser = new User("New", "User", "new.user@example.com", "newpassword");
        User savedUser = new User("New", "User", "new.user@example.com", "newpassword");
        savedUser.setId(3L);

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Create User - Throws exception when email already exists")
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        User newUser = new User("New", "User", "john.doe@example.com", "newpassword");
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.createUser(newUser));
        assertEquals(UserException.UserExceptionType.DUPLICATE_EMAIL, exception.getType());
        assertTrue(exception.getMessage().contains("already in use"));
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Returns updated user")
    void updateUser_ValidUpdate_ReturnsUpdatedUser() {
        // Arrange
        User updatedDetails = new User("John", "Updated", "john.updated@example.com", "newpassword");
        User existingUser = testUser1; // John Doe
        User savedUser = new User("John", "Updated", "john.updated@example.com", "newpassword");
        savedUser.setId(existingUser.getId());
        savedUser.setCreatedAt(existingUser.getCreatedAt());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Updated", result.getLastName());
        assertEquals("john.updated@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Throws exception when user not found")
    void updateUser_UserNotFound_ThrowsException() {
        // Arrange
        User updatedDetails = new User("John", "Updated", "john.updated@example.com", "newpassword");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.updateUser(999L, updatedDetails));
        assertEquals(UserException.UserExceptionType.NOT_FOUND, exception.getType());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Throws exception when email already exists for another user")
    void updateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        User updatedDetails = new User("John", "Updated", "jane.smith@example.com", "newpassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.updateUser(1L, updatedDetails));
        assertEquals(UserException.UserExceptionType.DUPLICATE_EMAIL, exception.getType());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("jane.smith@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Delete User - Successfully deletes user")
    void deleteUser_UserExists_DeletesSuccessfully() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete User - Throws exception when user not found")
    void deleteUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.deleteUser(999L));
        assertEquals(UserException.UserExceptionType.NOT_FOUND, exception.getType());
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
} 