package com.viceri.desafio.todo.service;

import com.viceri.desafio.todo.domain.dto.request.UserRegisterRequest;
import com.viceri.desafio.todo.domain.dto.response.UserRegisterResponse;
import com.viceri.desafio.todo.domain.exception.user.DuplicateEmailException;
import com.viceri.desafio.todo.domain.exception.user.WeakPasswordException;
import com.viceri.desafio.todo.domain.model.User;
import com.viceri.desafio.todo.domain.validation.PasswordValidator;
import com.viceri.desafio.todo.repository.UserRepository;
import com.viceri.desafio.todo.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRegisterRequest();
        validRequest.setName("Test User");
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("Secure123!");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        UserRegisterResponse response = userService.registerUser(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(validRequest.getName(), response.getName());
        assertEquals(validRequest.getEmail(), response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail(validRequest.getEmail());
        
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(validRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWeak() {
        // Arrange
        validRequest.setPassword("weakpassword");

        // Act & Assert
        assertThrows(WeakPasswordException.class, () -> userService.registerUser(validRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldValidatePasswordWithPasswordValidator() {
        assertTrue(PasswordValidator.isValid("Secure123!"));
        assertFalse(PasswordValidator.isValid("weak"));
        assertFalse(PasswordValidator.isValid("withoutuppercase1!"));
        assertFalse(PasswordValidator.isValid("WITHOUTLOWERCASE1!"));
        assertFalse(PasswordValidator.isValid("WithoutNumber!"));
        assertFalse(PasswordValidator.isValid("WithoutSpecial1"));
    }
}