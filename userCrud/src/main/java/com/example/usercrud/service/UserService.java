package com.example.usercrud.service;

import com.example.usercrud.dao.UserDao;
import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.User;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserResponse createUser(CreateUserRequest request) {
        validateCreateRequest(request);

        User user = UserMapper.toEntity(request);
        normalizeUser(user);

        User savedUser = userDao.save(user);
        logger.info("User created successfully with id={}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {
        validateId(id);

        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));

        logger.info("User fetched successfully by id={}", id);
        return UserMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userDao.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        logger.info("Fetched {} users", users.size());
        return users;
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        validateUpdateRequest(request);

        User existingUser = userDao.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("User with id=" + request.getId() + " not found"));

        UserMapper.updateEntity(existingUser, request);
        normalizeUser(existingUser);

        User updatedUser = userDao.update(existingUser);
        logger.info("User updated successfully with id={}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        validateId(id);

        boolean deleted = userDao.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("User with id=" + id + " not found");
        }

        logger.info("User deleted successfully with id={}", id);
    }

    private void validateCreateRequest(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create request must not be null");
        }
        validateUserFields(request.getName(), request.getEmail(), request.getAge());
    }

    private void validateUpdateRequest(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }
        validateId(request.getId());
        validateUserFields(request.getName(), request.getEmail(), request.getAge());
    }

    private void validateUserFields(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (age == null || age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
    }

    private void normalizeUser(User user) {
        if (user == null) {
            return;
        }

        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }

        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim());
        }
    }
}