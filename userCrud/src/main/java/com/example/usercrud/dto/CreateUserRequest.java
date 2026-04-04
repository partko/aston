package com.example.usercrud.dto;

/**
 * DTO для создания нового пользователя.
 */
public class CreateUserRequest {
    private String name;
    private String email;
    private Integer age;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String name, String email, Integer age) {
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