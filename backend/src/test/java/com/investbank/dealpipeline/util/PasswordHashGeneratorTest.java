package com.investbank.dealpipeline.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashGeneratorTest {

    @Test
    void shouldGenerateValidBCryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "testPassword123";
        
        String hash = encoder.encode(password);
        
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        assertTrue(encoder.matches(password, hash));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        
        assertNotEquals(hash1, hash2);
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }

    @Test
    void shouldMatchAdmin123Hash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        String hash = encoder.encode(password);
        
        assertTrue(encoder.matches(password, hash));
        assertFalse(encoder.matches("wrongpassword", hash));
    }

    @Test
    void shouldMatchUser123Hash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "user123";
        
        String hash = encoder.encode(password);
        
        assertTrue(encoder.matches(password, hash));
        assertFalse(encoder.matches("admin123", hash));
    }

    @Test
    void shouldHandleEmptyPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "";
        
        String hash = encoder.encode(password);
        
        assertNotNull(hash);
        assertTrue(encoder.matches(password, hash));
    }

    @Test
    void shouldHandleSpecialCharactersInPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "P@ssw0rd!#$%^&*()";
        
        String hash = encoder.encode(password);
        
        assertNotNull(hash);
        assertTrue(encoder.matches(password, hash));
        assertFalse(encoder.matches("P@ssw0rd", hash));
    }

    @Test
    void shouldGenerateHashWithDefaultStrength() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "testPassword";
        
        String hash = encoder.encode(password);
        
        // BCrypt hashes should be 60 characters long
        assertEquals(60, hash.length());
    }
}
