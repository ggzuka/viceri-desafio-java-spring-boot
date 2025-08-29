package com.viceri.desafio.todo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viceri.desafio.todo.domain.dto.request.UserLoginRequest;
import com.viceri.desafio.todo.domain.dto.request.UserRegisterRequest;
import com.viceri.desafio.todo.domain.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Registrar um usuário para testes de login
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test.login@example.com");
        registerRequest.setPassword("Secure123!");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test.login@example.com");
        loginRequest.setPassword("Secure123!");

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(responseContent, AuthResponse.class);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getToken().length() > 0);
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test.login@example.com");
        loginRequest.setPassword("WrongPassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("AnyPassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}