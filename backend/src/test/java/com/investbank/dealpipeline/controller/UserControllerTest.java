package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.TestApplication;
import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.security.JwtTokenProvider;
import com.investbank.dealpipeline.security.UserDetailsServiceImpl;
import com.investbank.dealpipeline.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
    properties = "spring.main.banner-mode=off")
@ContextConfiguration(classes = TestApplication.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldGetCurrentUserSuccessfully() throws Exception {
        UserResponse mockUserResponse = UserResponse.builder()
                .id("user123")
                .username("testuser")
                .role(Role.USER)
                .active(true)
                .build();
        
        when(userService.getUserByUsername("testuser")).thenReturn(mockUserResponse);
        
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldGetCurrentUserForAdmin() throws Exception {
        UserResponse mockAdminResponse = UserResponse.builder()
                .id("admin123")
                .username("admin")
                .role(Role.ADMIN)
                .active(true)
                .build();
        
        when(userService.getUserByUsername("admin")).thenReturn(mockAdminResponse);
        
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
