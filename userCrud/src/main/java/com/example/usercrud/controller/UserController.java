package com.example.usercrud.controller;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * REST API для управления пользователями.
 */
public interface UserController {

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными пользователя
     */
    @GetMapping("/{id}")
    UserResponse getById(@PathVariable Long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей в виде DTO
     */
    @GetMapping
    List<UserResponse> getAll();

    /**
     * Создаёт нового пользователя.
     *
     * @param request DTO с данными для создания пользователя
     * @return DTO созданного пользователя
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@Valid @RequestBody CreateUserRequest request);

    /**
     * Обновляет существующего пользователя.
     *
     * @param id идентификатор пользователя
     * @param request DTO с новыми данными пользователя
     * @return DTO обновлённого пользователя
     */
    @PutMapping("/{id}")
    UserResponse update(@PathVariable Long id,
                        @Valid @RequestBody UpdateUserRequest request);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id);
}