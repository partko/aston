package com.example.usercrud.service.impl;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.mapper.UserMapper;
import com.example.usercrud.repository.UserRepository;
import com.example.usercrud.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервисного слоя для работы с пользователями.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        String normalizedEmail = request.getEmail().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        UserEntity user = UserMapper.toEntity(request);
        user.setEmail(normalizedEmail);

        UserEntity savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан с id={}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        logger.info("Пользователь успешно получен по id={}", id);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        logger.info("Получено {} пользователей", users.size());
        return users;
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        String normalizedEmail = request.getEmail().trim();

        userRepository.findByEmail(normalizedEmail)
                        .filter(found -> !found.getId().equals(id))
                                .ifPresent(found -> {
                                    throw new IllegalArgumentException("Пользователь с таким email уже существует");
                                });

        UserMapper.updateEntity(existingUser, request);
        existingUser.setEmail(normalizedEmail);

        UserEntity updatedUser = userRepository.save(existingUser);
        logger.info("Пользователь успешно обновлён с id={}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
        logger.info("Пользователь успешно удалён с id={}", id);
    }
}