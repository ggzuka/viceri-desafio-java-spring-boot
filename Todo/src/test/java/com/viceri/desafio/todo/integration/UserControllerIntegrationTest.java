package com.viceri.desafio.todo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viceri.desafio.todo.domain.dto.request.UserRegisterRequest;
import com.viceri.desafio.todo.domain.dto.response.UserRegisterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("Integration Test User");
        request.setEmail("integration.test@example.com");
        request.setPassword("Secure123!");

        // Act
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        UserRegisterResponse response = objectMapper.readValue(responseContent, UserRegisterResponse.class);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Integration Test User", response.getName());
        assertEquals("integration.test@example.com", response.getEmail());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange - Primeiro registro
        UserRegisterRequest firstRequest = new UserRegisterRequest();
        firstRequest.setName("First User");
        firstRequest.setEmail("duplicate@example.com");
        firstRequest.setPassword("Secure123!");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Arrange - Segundo registro com mesmo email
        UserRegisterRequest secondRequest = new UserRegisterRequest();
        secondRequest.setName("Second User");
        secondRequest.setEmail("duplicate@example.com");
        secondRequest.setPassword("Secure123!");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsWeak() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("Test User");
        request.setEmail("test.weak@example.com");
        request.setPassword("weak"); // Senha fraca

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}