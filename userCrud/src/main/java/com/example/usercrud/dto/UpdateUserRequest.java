package com.example.usercrud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для обновления существующего пользователя.
 */
public class UpdateUserRequest {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неправильный формат email")
    private String email;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст должен быть в пределах от 0 до 150")
    @Max(value = 150, message = "Возраст должен быть в пределах от 0 до 150")
    private Integer age;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает возраст пользователя.
     *
     * @return возраст пользователя
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param age возраст пользователя
     */
    public void setAge(Integer age) {
        this.age = age;
    }
}