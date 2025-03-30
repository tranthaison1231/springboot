package com.shopdevjava.springboot.service;

import com.shopdevjava.springboot.dto.UserDTO;
import com.shopdevjava.springboot.dto.UserRequest;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserRequest userRequest);
    UserDTO updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
} 