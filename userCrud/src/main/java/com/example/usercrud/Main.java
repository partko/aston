package com.example.usercrud;

import com.example.usercrud.config.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения");

        try {
            new ApplicationRunner().run();
            logger.info("Приложение корректно завершило работу");
        } catch (Exception e) {
            logger.error("Ошибка при запуске или выполнении приложения", e);
        } finally {
            HibernateUtil.shutdown();
            logger.info("Ресурсы приложения освобождены");
        }
    }
}
