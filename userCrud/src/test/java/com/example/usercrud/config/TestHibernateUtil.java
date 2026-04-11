package com.example.usercrud.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Утилитный класс для создания SessionFactory в интеграционных тестах.
 */
public final class TestHibernateUtil {

    private TestHibernateUtil() {
    }

    /**
     * Создаёт SessionFactory для интеграционных тестов
     * на основе тестовой Hibernate-конфигурации и параметров контейнера PostgreSQL.
     *
     * @param postgres контейнер PostgreSQL
     * @return SessionFactory для тестов
     */
    public static SessionFactory buildSessionFactory(PostgreSQLContainer<?> postgres) {
        return new Configuration()
                .configure("hibernate-test.cfg.xml")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .buildSessionFactory();
    }
}