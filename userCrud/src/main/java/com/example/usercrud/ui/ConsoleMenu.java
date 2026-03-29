package com.example.usercrud.ui;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            if ("0".equals(choice)) {
                running = false;
                System.out.println("Exiting application.");
                continue;
            }

            ConsoleExceptionHandler.handle(() -> {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> listUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    default -> System.out.println("Unknown command. Try again.");
                }
            });
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("=== USER SERVICE ===");
        System.out.println("1. Create user");
        System.out.println("2. Find user by ID");
        System.out.println("3. Show all users");
        System.out.println("4. Update user");
        System.out.println("5. Delete user");
        System.out.println("0. Exit");
        System.out.print("Select action: ");
    }

    private void createUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        Integer age = readInt("Enter age: ");

        CreateUserRequest request = new CreateUserRequest(name, email, age);
        UserResponse response = userService.createUser(request);

        System.out.println("User created: " + response);
    }

    private void findUserById() {
        Long id = readLong("Enter user ID: ");
        UserResponse response = userService.getUserById(id);
        System.out.println(response);
    }

    private void listUsers() {
        List<UserResponse> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("User list is empty.");
            return;
        }
        users.forEach(System.out::println);
    }

    private void updateUser() {
        Long id = readLong("Enter user ID to update: ");

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();

        System.out.print("Enter new email: ");
        String email = scanner.nextLine();

        Integer age = readInt("Enter new age: ");

        UpdateUserRequest request = new UpdateUserRequest(id, name, email, age);
        UserResponse response = userService.updateUser(request);

        System.out.println("User updated: " + response);
    }

    private void deleteUser() {
        Long id = readLong("Enter user ID to delete: ");
        userService.deleteUser(id);
        System.out.println("User deleted.");
    }

    private Long readLong(String prompt) {
        System.out.print(prompt);
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid integer number");
        }
    }

    private Integer readInt(String line) {
        System.out.print(line);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid integer number");
        }
    }
}