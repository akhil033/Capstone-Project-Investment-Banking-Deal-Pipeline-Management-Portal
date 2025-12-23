package com.investbank.dealpipeline.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    private String jwtSecret = "YW55X3NlY3JldF9rZXlfZm9yX2RlYWxfcGlwZWxpbmVfbWFuYWdlbWVudF9wb3J0YWxfc3lzdGVt";
    private long jwtExpiration = 86400000L;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", jwtExpiration);
    }
    
    @Test
    void generateToken_Success() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        String token = jwtTokenProvider.generateToken(authentication);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void getUsernameFromToken_Success() {
        String username = "testuser";
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
        
        String result = jwtTokenProvider.getUsernameFromToken(token);
        
        assertEquals(username, result);
    }
    
    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
        
        boolean result = jwtTokenProvider.validateToken(token);
        
        assertTrue(result);
    }
    
    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";
        
        boolean result = jwtTokenProvider.validateToken(invalidToken);
        
        assertFalse(result);
    }
    
    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();
        
        boolean result = jwtTokenProvider.validateToken(token);
        
        assertFalse(result);
    }
}
