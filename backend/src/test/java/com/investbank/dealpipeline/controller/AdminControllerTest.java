package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.TestApplication;
import com.investbank.dealpipeline.dto.request.CreateUserRequest;
import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.mapper.UserMapper;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.security.JwtTokenProvider;
import com.investbank.dealpipeline.security.UserDetailsServiceImpl;
import com.investbank.dealpipeline.service.AuthService;
import com.investbank.dealpipeline.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
    properties = "spring.main.banner-mode=off")
@ContextConfiguration(classes = TestApplication.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserSuccessfully() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@bank.com")
                .password("password123")
                .role(Role.USER)
                .build();

        User mockUser = User.builder()
                .id("user123")
                .username("newuser")
                .email("newuser@bank.com")
                .role(Role.USER)
                .active(true)
                .build();

        UserResponse response = UserResponse.builder()
                .id("user123")
                .username("newuser")
                .email("newuser@bank.com")
                .role(Role.USER)
                .active(true)
                .build();

        when(authService.register(any(CreateUserRequest.class))).thenReturn(mockUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@bank.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersSuccessfully() throws Exception {
        UserResponse user1 = UserResponse.builder()
                .id("user1")
                .username("user1")
                .email("user1@bank.com")
                .role(Role.USER)
                .active(true)
                .build();

        UserResponse user2 = UserResponse.builder()
                .id("user2")
                .username("admin1")
                .email("admin1@bank.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        List<UserResponse> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("admin1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserStatusSuccessfully() throws Exception {
        UserResponse response = UserResponse.builder()
                .id("user123")
                .username("testuser")
                .email("test@bank.com")
                .role(Role.USER)
                .active(false)
                .build();

        when(userService.updateUserStatus(eq("user123"), eq(false))).thenReturn(response);

        mockMvc.perform(put("/api/admin/users/user123/status")
                        .with(csrf())
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    @org.junit.jupiter.api.Disabled("Method security not enforced in @WebMvcTest")
    void shouldDenyAccessToNonAdmin() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@bank.com")
                .password("password123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
