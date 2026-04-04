package com.example.usercrud.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Утилитный класс для управления Liquibase.
 */
public final class LiquibaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseUtil.class);

    private static final String URL = AppConfig.get("db.url");
    private static final String USERNAME = AppConfig.get("db.user");
    private static final String PASSWORD = AppConfig.get("db.password");

    private LiquibaseUtil() {
    }

    /**
     * Запускает миграцию БД.
     */
    public static void runMigrations() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
            logger.info("Liquibase-миграция была успешно осуществлена");
        } catch (LiquibaseException | java.sql.SQLException e) {
            logger.error("Ошибка при выполнении Liquibase-миграции", e);
            throw new RuntimeException("Невозможно выполнить Liquibase-миграцию", e);
        }
    }
}