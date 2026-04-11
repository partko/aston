package com.example.usercrud.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитный класс для создания и управления SessionFactory.
 */
public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final String CONFIG_FILE =
            System.getProperty("hibernate.config.file", "hibernate.cfg.xml");

    public static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {
    }

    /**
     * Создаёт SessionFactory на основе файла конфигурации Hibernate.
     *
     * @return объект SessionFactory
     */
    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Загрузка Hibernate-конфигурации из файла: {}", CONFIG_FILE);
            Configuration configuration = new Configuration();
            configuration.configure(CONFIG_FILE);

            logger.info("Применение настроек из AppConfig");
            configuration.setProperty("hibernate.connection.url", AppConfig.get("db.url"));
            configuration.setProperty("hibernate.connection.username", AppConfig.get("db.user"));
            configuration.setProperty("hibernate.connection.password", AppConfig.get("db.password"));

            return configuration.buildSessionFactory();
        } catch (Throwable e) {
            logger.error("Ошибка инициализации SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Возвращает общий экземпляр SessionFactory.
     *
     * @return объект SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Корректно завершает работу SessionFactory.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Закрытие SessionFactory");
            sessionFactory.close();
        }
    }
}
