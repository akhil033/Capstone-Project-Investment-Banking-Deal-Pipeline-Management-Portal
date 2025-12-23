package com.investbank.dealpipeline.mapper;

import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldMapUserToUserResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@bank.com")
                .password("hashedPassword")
                .role(Role.USER)
                .active(true)
                .createdAt(now)
                .build();

        // When
        UserResponse response = userMapper.toResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@bank.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.isActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        // When
        UserResponse response = userMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void shouldMapAdminUserCorrectly() {
        // Given
        User admin = User.builder()
                .id("admin123")
                .username("admin")
                .email("admin@bank.com")
                .password("hashedPassword")
                .role(Role.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        UserResponse response = userMapper.toResponse(admin);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void shouldMapInactiveUserCorrectly() {
        // Given
        User inactiveUser = User.builder()
                .id("user456")
                .username("inactiveuser")
                .email("inactive@bank.com")
                .password("hashedPassword")
                .role(Role.USER)
                .active(false)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        UserResponse response = userMapper.toResponse(inactiveUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isActive()).isFalse();
    }
}
