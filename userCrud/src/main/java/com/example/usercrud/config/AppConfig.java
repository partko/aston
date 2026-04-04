package com.example.usercrud.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для получения данных из application.properties.
 */
public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("Файл application.properties не найден");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении application.properties", e);
        }
    }

    /**
     * Возвращает property из application.properties по ключу.
     *
     * @param key ключ
     * @return значение по ключу
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }
}