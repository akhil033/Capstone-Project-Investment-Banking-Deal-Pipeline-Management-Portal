package com.investbank.dealpipeline.service;

import com.investbank.dealpipeline.dto.response.UserResponse;
import com.investbank.dealpipeline.exception.ResourceNotFoundException;
import com.investbank.dealpipeline.mapper.UserMapper;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserResponse getUserById(String id) {
        log.debug("Fetching user by ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        return userMapper.toResponse(user);
    }
    
    public UserResponse getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        return userMapper.toResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUserStatus(String id, boolean active) {
        log.debug("Updating user status: {} to {}", id, active);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setActive(active);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponse(updatedUser);
    }
}
