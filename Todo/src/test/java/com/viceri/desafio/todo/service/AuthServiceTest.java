package com.viceri.desafio.todo.service;

import com.viceri.desafio.todo.domain.exception.shared.UnauthorizedException;
import com.viceri.desafio.todo.domain.model.User;
import com.viceri.desafio.todo.repository.UserRepository;
import com.viceri.desafio.todo.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123!";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        // Act
        AuthService.AuthResult result = authService.authenticate(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(user.getId(), result.getUserId());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        String password = "Password123!";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(email, password));
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Arrange
        String email = "test@example.com";
        String password = "WrongPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(email, password));
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
    }
}