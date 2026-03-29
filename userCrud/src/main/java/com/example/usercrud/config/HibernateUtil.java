package com.example.usercrud.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    public static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil () {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            configuration.configure("hibernate.cfg.xml");

            configuration.setProperty("hibernate.connection.url", AppConfig.get("db.url"));
            configuration.setProperty("hibernate.connection.username", AppConfig.get("db.user"));
            configuration.setProperty("hibernate.connection.password", AppConfig.get("db.password"));

            return configuration.buildSessionFactory();
        } catch (Throwable e) {
            logger.error("Error in initialization SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing the SessionFactory");
            sessionFactory.close();
        }
    }
}
