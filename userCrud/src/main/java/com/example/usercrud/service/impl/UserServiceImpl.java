package com.example.usercrud.service.impl;

import com.example.usercrud.dao.UserDao;
import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.mapper.UserMapper;
import com.example.usercrud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Реализация сервисного слоя для работы с пользователями.
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        validateCreateRequest(request);

        String normalizedEmail = request.getEmail().trim();

        userDao.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        });

        UserEntity user = UserMapper.toEntity(request);
        normalizeUser(user);

        UserEntity savedUser = userDao.save(user);
        logger.info("Пользователь успешно создан с id={}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        validateId(id);

        UserEntity user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        logger.info("Пользователь успешно получен по id={}", id);
        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userDao.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        logger.info("Получено {} пользователей", users.size());
        return users;
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        validateUpdateRequest(request);

        UserEntity existingUser = userDao.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + request.getId() + " не найден"));

        UserMapper.updateEntity(existingUser, request);
        normalizeUser(existingUser);

        UserEntity updatedUser = userDao.update(existingUser);
        logger.info("Пользователь успешно обновлён с id={}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        validateId(id);

        boolean deleted = userDao.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }

        logger.info("Пользователь успешно удалён с id={}", id);
    }

    /**
     * Проверяет корректность DTO для создания пользователя.
     *
     * @param request DTO создания пользователя
     */
    private void validateCreateRequest(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос на создание не должен быть null");
        }
        validateUserFields(request.getName(), request.getEmail(), request.getAge());
    }

    /**
     * Проверяет корректность DTO для обновления пользователя.
     *
     * @param request DTO обновления пользователя
     */
    private void validateUpdateRequest(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }
        validateId(request.getId());
        validateUserFields(request.getName(), request.getEmail(), request.getAge());
    }

    /**
     * Проверяет корректность полей пользователя.
     *
     * @param name имя
     * @param email email
     * @param age возраст
     */
    private void validateUserFields(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не должно быть пустым");
        }
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
        if (age == null || age < 0 || age > 150) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 150");
        }
    }

    /**
     * Проверяет корректность идентификатора.
     *
     * @param id идентификатор
     */
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
    }

    /**
     * Нормализует строковые поля пользователя перед сохранением или обновлением.
     *
     * @param userEntity сущность пользователя
     */
    private void normalizeUser(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }

        if (userEntity.getName() != null) {
            userEntity.setName(userEntity.getName().trim());
        }

        if (userEntity.getEmail() != null) {
            userEntity.setEmail(userEntity.getEmail().trim());
        }
    }
}