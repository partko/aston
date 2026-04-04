package com.example.usercrud.dao.impl;

import com.example.usercrud.dao.BaseHibernateDao;
import com.example.usercrud.dao.UserDao;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Hibernate-реализация DAO для сущности пользователя.
 */
public class UserDaoImpl extends BaseHibernateDao<UserEntity, Long> implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    public UserDaoImpl() {
        super(UserEntity.class);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try {
            return executeInTransaction("поиск UserEntity по email", session ->
                    session.createQuery("from UserEntity where email = :email", UserEntity.class)
                            .setParameter("email", email)
                            .uniqueResultOptional()
            );
        } catch (Exception e) {
            logger.error("Ошибка при получении UserEntity по email={}", email, e);
            throw new DataAccessException("Не удалось получить UserEntity по email=" + email, e);
        }
    }
}
