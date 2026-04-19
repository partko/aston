package com.example.usercrud.dto;

import java.time.LocalDateTime;

/**
 * DTO для возврата данных пользователя во внешний слой.
 */
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, Integer age, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(Long id) {
        this.id = id;
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

    /**
     * Возвращает дату создания пользователя.
     *
     * @return дата создания пользователя
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания пользователя.
     *
     * @param createdAt дата создания пользователя
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User { "+ id
                + " | " + name
                + " | " + email
                + " | " + age
                + " | " + createdAt
                + " }";
    }
}
