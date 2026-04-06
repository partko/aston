package com.example.usercrud.service.impl;

import com.example.usercrud.dao.UserDao;
import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.UserEntity;
import com.example.usercrud.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);
        UserEntity savedEntity = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        savedEntity.setId(1L);
        savedEntity.setCreatedAt(LocalDateTime.now());
        when(userDao.findByEmail("vvv@gmail.com")).thenReturn(Optional.empty());
        when(userDao.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vladimir", response.getName());
        assertEquals("vvv@gmail.com", response.getEmail());
        assertEquals(25, response.getAge());

        verify(userDao).findByEmail("vvv@gmail.com");
        verify(userDao).save(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);

        UserEntity existingUser = new UserEntity("Existing", "vvv@gmail.com", 30);
        existingUser.setId(1000L);

        when(userDao.findByEmail("vvv@gmail.com")).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertNotNull(exception);
        verify(userDao).findByEmail("vvv@gmail.com");
        verify(userDao, never()).save(any());
        verifyNoMoreInteractions(userDao);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"email", "gmail@", "@gmail.com", "vvv.gmail.com", "vvv@"})
    void createUser_shouldThrowIllegalArgumentExceptionWhenEmailIsInvalid(String invalidEmail) {
        CreateUserRequest request = new CreateUserRequest("Vladimir", invalidEmail, 25);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertNotNull(exception);
        verifyNoInteractions(userDao);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 151, 1000})
    void createUser_shouldThrowIllegalArgumentExceptionWhenAgeIsInvalid(int invalidAge) {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", invalidAge);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertNotNull(exception);
        verifyNoInteractions(userDao);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void createUser_shouldThrowIllegalArgumentExceptionWhenNameIsInvalid(String invalidName) {
        CreateUserRequest request = new CreateUserRequest(invalidName, "vvv@gmail.com", 25);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertNotNull(exception);
        verifyNoInteractions(userDao);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void getUserById_shouldThrowIllegalArgumentExceptionWhenIdIsInvalid(long invalidId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(invalidId)
        );

        assertNotNull(exception);
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_shouldReturnUserSuccessfully() {
        UserEntity entity = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        when(userDao.findById(1L)).thenReturn(Optional.of(entity));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vladimir", response.getName());
        assertEquals("vvv@gmail.com", response.getEmail());
        assertEquals(25, response.getAge());
        assertNotNull(response.getCreatedAt());
        verify(userDao).findById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertNotNull(exception);
        verify(userDao).findById(1L);
        assertNotNull(exception);
    }

    @Test
    void getAllUsers_shouldReturnMappedUsers() {
        UserEntity first = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        first.setId(1L);
        first.setCreatedAt(LocalDateTime.now());

        UserEntity second = new UserEntity("Alice", "aaa@gmail.com", 22);
        second.setId(2L);
        second.setCreatedAt(LocalDateTime.now());

        when(userDao.findAll()).thenReturn(List.of(first, second));

        List<UserResponse> response = userService.getAllUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Vladimir", response.get(0).getName());
        assertEquals("Alice", response.get(1).getName());
        verify(userDao).findAll();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        UpdateUserRequest request = new UpdateUserRequest(1L, "Updated", "updated@gmail.com", 35);

        UserEntity existing = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now());

        UserEntity updated = new UserEntity("Updated", "updated@gmail.com", 35);
        updated.setId(1L);
        updated.setCreatedAt(existing.getCreatedAt());

        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(UserEntity.class))).thenReturn(updated);

        UserResponse response = userService.updateUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated", response.getName());
        assertEquals("updated@gmail.com", response.getEmail());
        assertEquals(35, response.getAge());

        verify(userDao).findById(1L);
        verify(userDao).update(argThat(entity ->
                entity.getId().equals(1L) &&
                        entity.getName().equals("Updated") &&
                        entity.getEmail().equals("updated@gmail.com") &&
                        entity.getAge() == 35
        ));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UpdateUserRequest request = new UpdateUserRequest(1L, "Updated", "updated@gmail.com", 35);
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(request)
        );

        assertNotNull(exception);
        verify(userDao).findById(1L);
        verify(userDao, never()).update(any());
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {
        when(userDao.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userDao).deleteById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userDao.deleteById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        assertNotNull(exception);
        verify(userDao).deleteById(1L);
        verifyNoMoreInteractions(userDao);
    }
}
