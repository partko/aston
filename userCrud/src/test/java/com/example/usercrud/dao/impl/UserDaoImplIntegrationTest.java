package com.example.usercrud.dao.impl;

import com.example.usercrud.config.TestHibernateUtil;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.UniqueConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты DAO слоя с использованием Testcontainers.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplIntegrationTest {
    private static SessionFactory sessionFactory;
    private static UserDaoImpl userDao;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        sessionFactory = TestHibernateUtil.buildSessionFactory(postgres);
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    @Test
    void save_shouldPersistUser() {
        UserEntity user = new UserEntity("Vladimir", "vvv@gmail.com", 25);

        UserEntity saved = userDao.save(user);

        assertNotNull(saved.getId());
        Optional<UserEntity> found = userDao.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Vladimir", found.get().getName());
        assertEquals("vvv@gmail.com", found.get().getEmail());
        assertEquals(25, found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        assertTrue(userDao.findById(10000L).isEmpty());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userDao.save(new UserEntity("Vladimir", "vvv@gmail.com", 25));
        userDao.save(new UserEntity("Alice", "aaa@gmail.com", 22));

        List<UserEntity> users = userDao.findAll();

        assertEquals(2, users.size());
        assertEquals("Vladimir", users.get(0).getName());
        assertEquals("Alice", users.get(1).getName());
    }

    @Test
    void update_shouldModifyExistingUser() {
        UserEntity user = userDao.save(new UserEntity("Vladimir", "vvv@gmail.com", 25));
        user.setName("Vladimir Updated");
        user.setEmail("updated@gmail.com");
        user.setAge(35);

        UserEntity updated = userDao.update(user);

        assertEquals("Vladimir Updated", updated.getName());
        assertEquals("updated@gmail.com", updated.getEmail());
        assertEquals(35, updated.getAge());
        Optional<UserEntity> found = userDao.findById(updated.getId());
        assertTrue(found.isPresent());
        assertEquals("Vladimir Updated", found.get().getName());
        assertEquals("updated@gmail.com", found.get().getEmail());
        assertEquals(35, found.get().getAge());
    }

    @Test
    void update_shouldThrowUniqueConstraintViolationExceptionWhenEmailIsDuplicated() {
        UserEntity first = userDao.save(new UserEntity("Ivan", "ivan@mail.com", 25));
        userDao.save(new UserEntity("Anna", "anna@mail.com", 30));
        first.setEmail("anna@mail.com");

        UniqueConstraintViolationException exception = assertThrows(
                UniqueConstraintViolationException.class,
                () -> userDao.update(first)
        );

        assertNotNull(exception);
    }

    @Test
    void deleteById_shouldDeleteExistingUser() {
        UserEntity user = userDao.save(new UserEntity("Vladimir", "vvv@gmail.com", 25));

        boolean deleted = userDao.deleteById(user.getId());

        assertTrue(deleted);
        assertTrue(userDao.findById(user.getId()).isEmpty());
    }

    @Test
    void deleteById_shouldReturnFalseWhenUserDoesNotExist() {
        boolean deleted = userDao.deleteById(1000L);

        assertFalse(deleted);
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        userDao.save(new UserEntity("Vladimir", "vvv@gmail.com", 25));

        Optional<UserEntity> found = userDao.findByEmail("vvv@gmail.com");

        assertTrue(found.isPresent());
        assertEquals("Vladimir", found.get().getName());
        assertEquals("vvv@gmail.com", found.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        Optional<UserEntity> found = userDao.findByEmail("missing@mail.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void save_shouldThrowUniqueConstraintViolationExceptionWhenEmailIsDuplicated() {
        userDao.save(new UserEntity("Vladimir", "vvv@gmail.com", 25));
        UserEntity duplicate = new UserEntity("Another Vladimir", "vvv@gmail.com", 40);

        UniqueConstraintViolationException exception = assertThrows(
                UniqueConstraintViolationException.class,
                () -> userDao.save(duplicate)
        );

        assertNotNull(exception);
    }

    /**
     * Полностью очищает таблицу users перед каждым тестом,
     * чтобы тесты были изолированы друг от друга.
     */
    private void cleanDatabase() {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("delete from UserEntity").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}