package com.example.usercrud.controller;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.exception.GlobalExceptionHandler;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты REST-контроллера пользователей.
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/users/{id} должен вернуть пользователя по id")
    void getById_shouldReturnUserById() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Vladimir",
                "vvv@gmail.com",
                20,
                LocalDateTime.now()
        );
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vladimir"))
                .andExpect(jsonPath("$.email").value("vvv@gmail.com"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userService).getUserById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("GET /api/users/{id} должен вернуть 404 если пользователь не найден")
    void getById_shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getUserById(1000L))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/api/users/1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray());

        verify(userService).getUserById(1000L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("GET /api/users/ должен вернуть список пользователей")
    void getAll_shouldReturnUsersList() throws Exception {
        List<UserResponse> users = List.of(
                new UserResponse(1L, "Vladimir", "vvv@gmail.com", 20, LocalDateTime.now()),
                new UserResponse(2L, "Alice", "aaa@gmail.com", 25, LocalDateTime.now())
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Vladimir"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Alice"));

        verify(userService).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("GET /api/users/ должен вернуть пустой список, если пользователей нет")
    void getAll_shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен создать пользователя")
    void create_shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);
        UserResponse response = new UserResponse(
                1L,
                "Vladimir",
                "vvv@gmail.com",
                20,
                LocalDateTime.now()
        );
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vladimir"))
                .andExpect(jsonPath("$.email").value("vvv@gmail.com"))
                .andExpect(jsonPath("$.age").value(20));

        verify(userService).createUser(any(CreateUserRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен вернуть 400 если имя пустое")
    void create_shouldReturn400WhenNameIsBlank() throws Exception {
        CreateUserRequest request = new CreateUserRequest("", "vvv@gmail.com", 20);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен вернуть 400 если email некорректный")
    void create_shouldReturn400WhenEmailIsInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "wrong-email", 25);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен вернуть 400 если возраст больше 150")
    void create_shouldReturn400WhenAgeIsTooHigh() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 151);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен вернуть 400 если сервис бросает IllegalArgumentException")
    void create_shouldReturn400WhenServiceThrowsIllegalArgumentException() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Duplicate email"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.details").isArray());

        verify(userService).createUser(any(CreateUserRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users должен вернуть 400 если тело запроса невалидный JSON")
    void create_shouldReturn400WhenJsonIsMalformed() throws Exception {
        String malformedJson = """
                {
                  "name": "Vladimir",
                  "email": "vvv@gmail.com",
                  "age":
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("PUT /api/users/{id} должен обновить пользователя")
    void update_shouldUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Vladimir New", "new@gmail.com", 35);
        UserResponse response = new UserResponse(
                1L,
                "Vladimir New",
                "new@gmail.com",
                35,
                LocalDateTime.now()
        );
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vladimir New"))
                .andExpect(jsonPath("$.email").value("new@gmail.com"))
                .andExpect(jsonPath("$.age").value(35));

        verify(userService).updateUser(eq(1L), any(UpdateUserRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("PUT /api/users/{id} должен вернуть 400 если update request невалиден")
    void update_shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("", "wrong-email", -1);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("PUT /api/users/{id} должен вернуть 404 если обновляемый пользователь не найден")
    void update_shouldReturn404WhenUpdatingMissingUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Vladimir New", "new@gmail.com", 35);
        when(userService.updateUser(eq(1000L), any(UpdateUserRequest.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(put("/api/users/1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray());

        verify(userService).updateUser(eq(1000L), any(UpdateUserRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} должен удалить пользователя и вернуть 204")
    void delete_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} должен вернуть 404 если пользователь для удаления не найден")
    void delete_shouldReturn404WhenDeletingMissingUser() throws Exception {
        doThrow(new NotFoundException("User not found"))
                .when(userService).deleteUser(1000L);

        mockMvc.perform(delete("/api/users/1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray());

        verify(userService).deleteUser(1000L);
        verifyNoMoreInteractions(userService);
    }
}