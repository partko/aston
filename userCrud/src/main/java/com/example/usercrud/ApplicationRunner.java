package com.example.usercrud;

import com.example.usercrud.dao.UserDao;
import com.example.usercrud.dao.impl.UserDaoImpl;
import com.example.usercrud.service.UserService;
import com.example.usercrud.ui.ConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    public void run() {
        logger.info("Initializing application components");

        UserDao userDao = new UserDaoImpl();
        UserService userService = new UserService(userDao);
        ConsoleMenu consoleMenu = new ConsoleMenu(userService);

        logger.info("Application initialized successfully");
        consoleMenu.start();
    }
}
