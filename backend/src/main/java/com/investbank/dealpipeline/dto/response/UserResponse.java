package com.investbank.dealpipeline.dto.response;

import com.investbank.dealpipeline.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}
