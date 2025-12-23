package com.investbank.dealpipeline.service;

import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.exception.ResourceNotFoundException;
import com.investbank.dealpipeline.mapper.UserMapper;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    private User user;
    private UserResponse userResponse;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        userResponse = UserResponse.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void getUserById_Success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        UserResponse result = userService.getUserById("user123");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById("user123");
    }
    
    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById("user123"));
    }
    
    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        UserResponse result = userService.getUserByUsername("testuser");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        List<UserResponse> result = userService.getAllUsers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void updateUserStatus_Success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        UserResponse result = userService.updateUserStatus("user123", false);
        
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                userService.getUserByUsername("nonexistent"));
    }

    @Test
    void updateUserStatus_NotFound_ThrowsException() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                userService.updateUserStatus("user123", false));
    }

    @Test
    void getAllUsers_EmptyList_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        
        List<UserResponse> result = userService.getAllUsers();
        
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_MultipleUsers_Success() {
        User user2 = User.builder()
                .id("user456")
                .username("testuser2")
                .email("test2@example.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        UserResponse userResponse2 = UserResponse.builder()
                .id("user456")
                .username("testuser2")
                .email("test2@example.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);
        
        List<UserResponse> result = userService.getAllUsers();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).toResponse(any(User.class));
    }

    @Test
    void updateUserStatus_Activate_Success() {
        user.setActive(false);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        UserResponse result = userService.updateUserStatus("user123", true);
        
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
