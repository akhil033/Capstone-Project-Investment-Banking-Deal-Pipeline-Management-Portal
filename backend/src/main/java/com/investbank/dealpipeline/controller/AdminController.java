package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.dto.request.CreateUserRequest;
import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.mapper.UserMapper;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.service.AuthService;
import com.investbank.dealpipeline.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = authService.register(request);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable String id,
            @RequestParam boolean active) {
        
        UserResponse response = userService.updateUserStatus(id, active);
        return ResponseEntity.ok(response);
    }
}
