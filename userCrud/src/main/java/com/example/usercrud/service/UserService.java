package com.example.usercrud.service;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;

import java.util.List;

/**
 * Контракт сервисного слоя для бизнес-операций с пользователями.
 */
public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UpdateUserRequest request);
    void deleteUser(Long id);
}
