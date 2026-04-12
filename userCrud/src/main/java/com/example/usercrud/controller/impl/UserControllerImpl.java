package com.example.usercrud.controller.impl;

import com.example.usercrud.controller.UserController;
import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserResponse getById(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public List<UserResponse> getAll() {
        return userService.getAllUsers();
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        return userService.createUser(request);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @Override
    public void delete(Long id) {
        userService.deleteUser(id);
    }
}