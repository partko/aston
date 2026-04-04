package com.example.usercrud.dto;

/**
 * DTO для обновления существующего пользователя.
 */
public class UpdateUserRequest {
    private Long id;
    private String name;
    private String email;
    private Integer age;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(Long id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
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
}