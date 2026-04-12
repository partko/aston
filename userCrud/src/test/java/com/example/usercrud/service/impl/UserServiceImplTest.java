package com.example.usercrud.service.impl;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты сервисного слоя пользователей.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("createUser должен создать пользователя")
    void createUser_shouldCreateUser() {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);
        UserEntity savedEntity = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        savedEntity.setId(1L);
        savedEntity.setCreatedAt(LocalDateTime.now());
        when(userRepository.existsByEmail("vvv@gmail.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vladimir", response.getName());
        assertEquals("vvv@gmail.com", response.getEmail());
        assertEquals(25, response.getAge());
        verify(userRepository).existsByEmail("vvv@gmail.com");
        verify(userRepository).save(argThat(matchesUser("Vladimir", "vvv@gmail.com", 25)));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("createUser должен бросить IllegalArgumentException если email уже занят")
    void createUser_shouldThrowIllegalArgumentExceptionWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);
        when(userRepository.existsByEmail("vvv@gmail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));

        verify(userRepository).existsByEmail("vvv@gmail.com");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getUserById должен вернуть пользователя по id")
    void getUserById_shouldReturnUserById() {
        UserEntity entity = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vladimir", response.getName());
        assertEquals("vvv@gmail.com", response.getEmail());
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getUserById должен бросить NotFoundException если пользователь не найден")
    void getUserById_shouldThrowNotFoundExceptionWhenUserMissing() {
        when(userRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1000L));

        verify(userRepository).findById(1000L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getAllUsers должен вернуть всех пользователей")
    void getAllUsers_shouldReturnAllUsers() {
        List<UserEntity> entities = List.of(
                new UserEntity("Vladimir", "vvv@gmail.com", 25),
                new UserEntity("Alice", "aaa@gmail.com", 30)
        );
        when(userRepository.findAll()).thenReturn(entities);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vladimir", result.get(0).getName());
        assertEquals("Alice", result.get(1).getName());
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getAllUsers должен вернуть пустой список если пользователей нет")
    void getAllUsers_shouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("updateUser должен обновить пользователя")
    void updateUser_shouldUpdateUser() {
        UpdateUserRequest request = new UpdateUserRequest("New", "new@gmail.com", 35);
        UserEntity existing = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now());
        UserEntity updated = new UserEntity("New", "new@gmail.com", 35);
        updated.setId(1L);
        updated.setCreatedAt(existing.getCreatedAt());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(updated);

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New", response.getName());
        assertEquals("new@gmail.com", response.getEmail());
        assertEquals(35, response.getAge());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("new@gmail.com");
        verify(userRepository).save(argThat(matchesUser("New", "new@gmail.com", 35)));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("updateUser должен бросить NotFoundException если обновляемый пользователь не найден")
    void updateUser_shouldThrowNotFoundExceptionWhenUpdatingMissingUser() {
        UpdateUserRequest request = new UpdateUserRequest("New", "new@gmail.com", 35);
        when(userRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(1000L, request));

        verify(userRepository).findById(1000L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("updateUser должен бросить IllegalArgumentException если новый email занят другим пользователем")
    void updateUser_shouldThrowIllegalArgumentExceptionWhenEmailBelongsToAnotherUser() {
        UpdateUserRequest request = new UpdateUserRequest("Updated", "occupied@gmail.com", 35);
        UserEntity existing = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now());
        UserEntity anotherUser = new UserEntity("Alice", "occupied@gmail.com", 30);
        anotherUser.setId(2L);
        anotherUser.setCreatedAt(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("occupied@gmail.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, request));

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("occupied@gmail.com");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("updateUser должен обновить пользователя если email принадлежит ему же")
    void updateUser_shouldUpdateUserWhenEmailBelongsToSameUser() {
        UpdateUserRequest request = new UpdateUserRequest("Vladimir New", "vvv@gmail.com", 40);
        UserEntity existing = new UserEntity("Vladimir", "vvv@mail.com", 25);
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now());
        UserEntity sameUserByEmail = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        sameUserByEmail.setId(1L);
        sameUserByEmail.setCreatedAt(existing.getCreatedAt());
        UserEntity saved = new UserEntity("Vladimir New", "vvv@gmail.com", 40);
        saved.setId(1L);
        saved.setCreatedAt(existing.getCreatedAt());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("vvv@gmail.com")).thenReturn(Optional.of(sameUserByEmail));
        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        assertEquals("Vladimir New", response.getName());
        assertEquals("vvv@gmail.com", response.getEmail());
        assertEquals(40, response.getAge());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("vvv@gmail.com");
        verify(userRepository).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("deleteUser должен удалить пользователя")
    void deleteUser_shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("deleteUser должен бросить NotFoundException если пользователь для удаления не найден")
    void deleteUser_shouldThrowNotFoundExceptionWhenDeletingMissingUser() {
        when(userRepository.existsById(1000L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1000L));

        verify(userRepository).existsById(1000L);
        verify(userRepository, never()).deleteById(any());
        verifyNoMoreInteractions(userRepository);
    }

    private ArgumentMatcher<UserEntity> matchesUser(String expectedName, String expectedEmail, Integer expectedAge) {
        return entity ->
                entity != null &&
                        expectedName.equals(entity.getName()) &&
                        expectedEmail.equals(entity.getEmail()) &&
                        expectedAge.equals(entity.getAge());
    }
}