package com.investbank.dealpipeline.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        authException = new BadCredentialsException("Invalid credentials");
    }

    @Test
    void shouldSendUnauthorizedError() throws IOException, ServletException {
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Test
    void shouldHandleAuthenticationException() throws IOException, ServletException {
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        jwtAuthenticationEntryPoint.commence(request, response, exception);

        verify(response).sendError(401, "Unauthorized");
    }

    @Test
    void shouldHandleNullAuthException() throws IOException, ServletException {
        AuthenticationException nullException = new BadCredentialsException(null);

        jwtAuthenticationEntryPoint.commence(request, response, nullException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
