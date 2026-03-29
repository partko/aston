package com.example.usercrud.dao.impl;

import com.example.usercrud.dao.BaseHibernateDao;
import com.example.usercrud.dao.UserDao;
import com.example.usercrud.entity.User;
import com.example.usercrud.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserDaoImpl extends BaseHibernateDao<User, Long> implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return executeInTransaction("find User by email", session ->
                    session.createQuery("from User where email = :email", User.class)
                            .setParameter("email", email)
                            .uniqueResultOptional()
            );
        } catch (Exception e) {
            logger.error("Error fetching User by email={}", email, e);
            throw new DataAccessException("Failed to fetch User by email=" + email, e);
        }
    }
}
