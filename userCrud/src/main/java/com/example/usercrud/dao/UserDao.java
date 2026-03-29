package com.example.usercrud.dao;

import com.example.usercrud.entity.User;

import java.util.Optional;

public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByEmail(String email);
}