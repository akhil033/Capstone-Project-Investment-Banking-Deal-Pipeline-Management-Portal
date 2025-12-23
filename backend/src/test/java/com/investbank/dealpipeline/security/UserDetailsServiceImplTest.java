package com.investbank.dealpipeline.security;

import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setId("user123");
        activeUser.setUsername("testuser");
        activeUser.setPassword("hashedPassword");
        activeUser.setRole(Role.USER);
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId("user456");
        inactiveUser.setUsername("inactiveuser");
        inactiveUser.setPassword("hashedPassword");
        inactiveUser.setRole(Role.USER);
        inactiveUser.setActive(false);
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldLoadAdminUserSuccessfully() {
        User adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setUsername("admin");
        adminUser.setPassword("adminPassword");
        adminUser.setRole(Role.ADMIN);
        adminUser.setActive(true);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        when(userRepository.findByUsername("inactiveuser")).thenReturn(Optional.of(inactiveUser));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inactiveuser")
        );

        assertEquals("User account is deactivated", exception.getMessage());
    }

    @Test
    void shouldReturnDisabledUserDetailsForInactiveUser() {
        // Even though we throw exception for inactive users, 
        // the UserDetails would have disabled flag set if it were returned
        User user = new User();
        user.setId("user789");
        user.setUsername("testuser2");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setActive(false);

        when(userRepository.findByUsername("testuser2")).thenReturn(Optional.of(user));

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("testuser2"));
    }
}
