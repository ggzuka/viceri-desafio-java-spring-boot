package com.viceri.desafio.todo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viceri.desafio.todo.domain.dto.request.TaskCreateRequest;
import com.viceri.desafio.todo.domain.dto.request.TaskUpdateRequest;
import com.viceri.desafio.todo.domain.dto.response.TaskResponse;
import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.security.JwtUtil;
import com.viceri.desafio.todo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtUtil jwtUtil;

    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        // Configure JWT mock
        when(jwtUtil.validateToken("validToken")).thenReturn(true);
        when(jwtUtil.extractUserId("validToken")).thenReturn(1L);
        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        // Create test response
        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setDescription("Test Task");
        taskResponse.setPriority(TaskPriority.MEDIA);
        taskResponse.setCompleted(false);
        taskResponse.setCreatedAt(LocalDateTime.now());
        taskResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setDescription("Test Task");
        request.setPriority(TaskPriority.MEDIA);

        when(taskService.createTask(any(TaskCreateRequest.class), eq(1L))).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test Task"))
                .andExpect(jsonPath("$.priority").value("MEDIA"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setDescription("Test Task");
        request.setPriority(TaskPriority.MEDIA);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer invalidToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(); // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isNoContent());
        
        verify(taskService).deleteTask(1L, 1L);
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setDescription("Updated Task");
        request.setPriority(TaskPriority.ALTA);

        TaskResponse updatedResponse = new TaskResponse();
        updatedResponse.setId(1L);
        updatedResponse.setDescription("Updated Task");
        updatedResponse.setPriority(TaskPriority.ALTA);
        updatedResponse.setCompleted(false);
        updatedResponse.setCreatedAt(LocalDateTime.now());
        updatedResponse.setUpdatedAt(LocalDateTime.now());

        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class), eq(1L))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/1")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated Task"))
                .andExpect(jsonPath("$.priority").value("ALTA"));
    }

    @Test
    void shouldGetUserTasksSuccessfully() throws Exception {
        // Arrange
        when(taskService.getUserPendingTasks(1L)).thenReturn(List.of(taskResponse));

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test Task"));
    }

    @Test
    void shouldGetUserTasksByPrioritySuccessfully() throws Exception {
        // Arrange
        when(taskService.getUserPendingTasksByPriority(1L, "ALTA")).thenReturn(List.of(taskResponse));

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer validToken")
                .param("priority", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test Task"));
    }
}