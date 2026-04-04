package com.example.usercrud.ui;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.service.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Консольный пользовательский интерфейс для работы с пользователями.
 */
public class ConsoleMenu {
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(UserService userService) {
        this.userService = userService;
    }

    /**
     * Запускает основной цикл консольного меню.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            if ("0".equals(choice)) {
                running = false;
                System.out.println("Выход из приложения.");
                continue;
            }

            ConsoleExceptionHandler.handle(() -> {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> listUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    default -> System.out.println("Неизвестная команда. Повторите ввод.");
                }
            });
            System.out.println();
        }
    }

    /**
     * Выводит меню доступных действий.
     */
    private void printMenu() {
        System.out.println("=== USER SERVICE ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    /**
     * Создаёт нового пользователя.
     */
    private void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        Integer age = readInt("Введите возраст: ");

        CreateUserRequest request = new CreateUserRequest(name, email, age);
        UserResponse response = userService.createUser(request);

        System.out.println("Пользователь создан: " + response);
    }

    /**
     * Ищет пользователя по идентификатору.
     */
    private void findUserById() {
        Long id = readLong("Введите ID пользователя: ");
        UserResponse response = userService.getUserById(id);
        System.out.println(response);
    }

    /**
     * Выводит список всех пользователей.
     */
    private void listUsers() {
        List<UserResponse> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
            return;
        }
        users.forEach(System.out::println);
    }

    /**
     * Обновляет существующего пользователя.
     */
    private void updateUser() {
        Long id = readLong("Введите ID пользователя для обновления: ");

        System.out.print("Введите новое имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите новый email: ");
        String email = scanner.nextLine();

        Integer age = readInt("Введите новый возраст: ");

        UpdateUserRequest request = new UpdateUserRequest(id, name, email, age);
        UserResponse response = userService.updateUser(request);

        System.out.println("Пользователь обновлён: " + response);
    }

    /**
     * Удаляет пользователя по идентификатору.
     */
    private void deleteUser() {
        Long id = readLong("Введите ID пользователя для удаления: ");
        userService.deleteUser(id);
        System.out.println("Пользователь удалён.");
    }

    /**
     * Считывает целое число типа Long из консоли.
     *
     * @param line ввод пользователя
     * @return введённое число
     */
    private Long readLong(String line) {
        System.out.print(line);
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Необходимо ввести корректное целое число");
        }
    }

    /**
     * Считывает целое число типа Integer из консоли.
     *
     * @param line ввод пользователя
     * @return введённое число
     */
    private Integer readInt(String line) {
        System.out.print(line);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Необходимо ввести корректное целое число");
        }
    }
}