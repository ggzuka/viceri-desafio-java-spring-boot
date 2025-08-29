package com.viceri.desafio.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viceri.desafio.todo.domain.dto.request.UserRegisterRequest;
import com.viceri.desafio.todo.domain.dto.response.UserRegisterResponse;
import com.viceri.desafio.todo.domain.exception.user.DuplicateEmailException;
import com.viceri.desafio.todo.domain.exception.user.WeakPasswordException;
import com.viceri.desafio.todo.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("Secure123!");

        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(1L);
        response.setName("John Doe");
        response.setEmail("john.doe@example.com");

        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("");
        request.setEmail("john.doe@example.com");
        request.setPassword("Secure123!");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("John Doe");
        request.setEmail("invalid-email");
        request.setPassword("Secure123!");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("John Doe");
        request.setEmail("existing@example.com");
        request.setPassword("Secure123!");

        when(userService.registerUser(any(UserRegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("Email já cadastrado"));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsWeak() throws Exception {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("WeakPassword");

        when(userService.registerUser(any(UserRegisterRequest.class)))
                .thenThrow(new WeakPasswordException("Senha não atende aos requisitos"));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalid() throws Exception {
        // Arrange - Request sem corpo

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}