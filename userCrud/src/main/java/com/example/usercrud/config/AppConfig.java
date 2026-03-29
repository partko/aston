package com.example.usercrud.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("File application.properties is not found");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Error while loading application.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}