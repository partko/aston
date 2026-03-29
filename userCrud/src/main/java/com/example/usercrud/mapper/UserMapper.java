package com.example.usercrud.mapper;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }
        return new User(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );
    }

    public static void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}