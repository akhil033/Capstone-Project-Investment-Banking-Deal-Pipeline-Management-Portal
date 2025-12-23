package com.investbank.dealpipeline.config;

import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create default admin user if no users exist
        if (userRepository.count() == 0) {
            log.info("No users found. Creating default users...");
            
            // Admin user
            User admin = User.builder()
                    .username("admin")
                    .email("admin@investbank.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            log.info("✓ Admin user created: admin / admin123");
            
            // User 1
            User user1 = User.builder()
                    .username("user1")
                    .email("user1@investbank.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user1);
            log.info("✓ User created: user1 / user123");
            
            // User 2
            User user2 = User.builder()
                    .username("user2")
                    .email("user2@investbank.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user2);
            log.info("✓ User created: user2 / user123");
            
            log.info("Data initialization completed successfully!");
        } else {
            log.info("Users already exist. Skipping initialization.");
        }
    }
}
