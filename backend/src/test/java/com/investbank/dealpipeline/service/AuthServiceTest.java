package com.investbank.dealpipeline.service;

import com.investbank.dealpipeline.dto.request.CreateUserRequest;
import com.investbank.dealpipeline.dto.request.LoginRequest;
import com.investbank.dealpipeline.dto.response.LoginResponse;
import com.investbank.dealpipeline.exception.BadRequestException;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import com.investbank.dealpipeline.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthService authService;
    
    private CreateUserRequest createUserRequest;
    private LoginRequest loginRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
        
        user = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = authService.register(createUserRequest);
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void register_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        assertThrows(BadRequestException.class, () -> authService.register(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void register_EmailExists_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        assertThrows(BadRequestException.class, () -> authService.register(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        
        LoginResponse result = authService.login(loginRequest);
        
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
    }
    
    @Test
    void login_InactiveUser_ThrowsException() {
        user.setActive(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        
        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }
}
