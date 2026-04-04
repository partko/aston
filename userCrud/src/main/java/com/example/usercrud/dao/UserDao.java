package com.example.usercrud.dao;

import com.example.usercrud.entity.UserEntity;

import java.util.Optional;

/**
 * DAO-контракт для операций, специфичных для пользователя.
 */
public interface UserDao extends GenericDao<UserEntity, Long> {
    /**
     * Ищет пользователя по email.
     *
     * @param email email пользователя
     * @return найденный пользователь или пустой результат
     */
    Optional<UserEntity> findByEmail(String email);
}