package com.example.usercrud.mapper;

import com.example.usercrud.dto.CreateUserRequest;
import com.example.usercrud.dto.UpdateUserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Юнит-тесты для UserMapper.
 */
class UserMapperTest {

    @Test
    void toEntity_shouldMapCreateUserRequestToUserEntity() {
        CreateUserRequest request = new CreateUserRequest("Vladimir", "vvv@gmail.com", 25);

        UserEntity result = UserMapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Vladimir", result.getName());
        assertEquals("vvv@gmail.com", result.getEmail());
        assertEquals(25, result.getAge());
        assertNull(result.getCreatedAt());
    }

    @Test
    void updateEntity_shouldUpdateExistingUserEntityFromRequest() {
        UserEntity userEntity = new UserEntity("Old Name", "old@gmail.com", 20);
        userEntity.setId(1L);
        userEntity.setCreatedAt(LocalDateTime.now());
        UpdateUserRequest request = new UpdateUserRequest(1L, "New Name", "new@gmail.com", 35);
        LocalDateTime createdAtBeforeUpdate = userEntity.getCreatedAt();

        UserMapper.updateEntity(userEntity, request);

        assertEquals(1L, userEntity.getId());
        assertEquals("New Name", userEntity.getName());
        assertEquals("new@gmail.com", userEntity.getEmail());
        assertEquals(35, userEntity.getAge());
        assertEquals(createdAtBeforeUpdate, userEntity.getCreatedAt());
    }

    @Test
    void updateEntity_shouldDoNothingWhenUserEntityIsNull() {
        UserEntity userEntity = null;
        UpdateUserRequest request = new UpdateUserRequest(1L, "New Name", "new@gmail.com", 35);

        UserMapper.updateEntity(userEntity, request);

        assertNull(userEntity);
    }

    @Test
    void updateEntity_shouldDoNothingWhenRequestIsNull() {
        UserEntity userEntity = new UserEntity("Old Name", "old@gmail.com", 20);
        userEntity.setId(1L);
        userEntity.setCreatedAt(LocalDateTime.now());
        String oldName = userEntity.getName();
        String oldEmail = userEntity.getEmail();
        Integer oldAge = userEntity.getAge();
        LocalDateTime oldCreatedAt = userEntity.getCreatedAt();
        UpdateUserRequest request = null;

        UserMapper.updateEntity(userEntity, request);

        assertEquals(1L, userEntity.getId());
        assertEquals(oldName, userEntity.getName());
        assertEquals(oldEmail, userEntity.getEmail());
        assertEquals(oldAge, userEntity.getAge());
        assertEquals(oldCreatedAt, userEntity.getCreatedAt());
    }

    @Test
    void toResponse_shouldMapUserEntityToUserResponse() {
        LocalDateTime createdAt = LocalDateTime.now();
        UserEntity userEntity = new UserEntity("Vladimir", "vvv@gmail.com", 25);
        userEntity.setId(1L);
        userEntity.setCreatedAt(createdAt);

        UserResponse result = UserMapper.toResponse(userEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Vladimir", result.getName());
        assertEquals("vvv@gmail.com", result.getEmail());
        assertEquals(25, result.getAge());
        assertEquals(createdAt, result.getCreatedAt());
    }
}