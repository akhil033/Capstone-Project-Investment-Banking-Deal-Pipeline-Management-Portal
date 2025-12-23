package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
}
