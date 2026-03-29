package com.example.usercrud;

import com.example.usercrud.config.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting user-service application");

        try {
            new ApplicationRunner().run();
            logger.info("Application finished successfully");
        } catch (Exception e) {
            logger.error("Application startup or execution failed", e);
        } finally {
            HibernateUtil.shutdown();
            logger.info("Application resources released");
        }
    }
}
