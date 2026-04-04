package com.example.usercrud;

import com.example.usercrud.dao.UserDao;
import com.example.usercrud.dao.impl.UserDaoImpl;
import com.example.usercrud.service.UserService;
import com.example.usercrud.service.impl.UserServiceImpl;
import com.example.usercrud.ui.ConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для инициализации компонентов консольного приложения
 */
public class ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    public void run() {
        logger.info("Инициализация компонентов приложения");

        UserDao userDao = new UserDaoImpl();
        UserService userService = new UserServiceImpl(userDao);
        ConsoleMenu consoleMenu = new ConsoleMenu(userService);

        logger.info("Компоненты приложения успешно инициализированы");
        consoleMenu.start();
    }
}
