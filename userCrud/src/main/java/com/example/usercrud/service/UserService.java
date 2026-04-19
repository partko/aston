package com.example.usercrud.service;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;

import java.util.List;

/**
 * Контракт сервисного слоя для бизнес-операций с пользователями.
 */
public interface UserService {

    /**
     * Создаёт нового пользователя.
     *
     * @param request DTO с данными для создания пользователя (имя, email, возраст)
     * @return DTO с данными созданного пользователя, включая сгенерированный id и дату создания
     * @throws IllegalArgumentException если пользователь с таким email уже существует
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными пользователя
     * @throws IllegalArgumentException если id некорректный (например, null или <= 0)
     * @throws com.example.usercrud.exception.NotFoundException если пользователь не найден
     */
    UserResponse getUserById(Long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO пользователей; если пользователей нет, возвращает пустой список
     */
    List<UserResponse> getAllUsers();

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id идентификатор пользователя
     * @param request DTO с новыми данными пользователя
     * @return DTO с обновлёнными данными пользователя
     * @throws IllegalArgumentException если id некорректный или email уже занят другим пользователем
     * @throws com.example.usercrud.exception.NotFoundException если пользователь не найден
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @throws IllegalArgumentException если id некорректный (например, null или <= 0)
     * @throws com.example.usercrud.exception.NotFoundException если пользователь не найден
     */
    void deleteUser(Long id);
}