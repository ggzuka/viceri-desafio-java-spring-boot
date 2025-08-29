package com.viceri.desafio.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.viceri.desafio.todo.config.CurrentUserIdResolverConfig;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CurrentUserIdResolverConfig.class)
class TaskControllerTest {

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
        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setDescription("Test Task");
        taskResponse.setPriority(TaskPriority.MEDIA);
        taskResponse.setCompleted(false);
        taskResponse.setCreatedAt(LocalDateTime.now());
        taskResponse.setUpdatedAt(LocalDateTime.now());

        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest();
        request.setDescription("Test Task");
        request.setPriority(TaskPriority.MEDIA);

        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.createTask(any(TaskCreateRequest.class), eq(1L))).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer mockToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test Task"))
                .andExpect(jsonPath("$.priority").value(TaskPriority.MEDIA.getDisplayName()));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        doNothing().when(taskService).deleteTask(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1")
                .header("Authorization", "Bearer mockToken"))
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

        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class), eq(1L))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/1")
                .header("Authorization", "Bearer mockToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated Task"))
                .andExpect(jsonPath("$.priority").value(TaskPriority.ALTA.getDisplayName()));
    }

    @Test
    void shouldMarkTaskAsCompleted() throws Exception {
        // Arrange
        TaskResponse completedResponse = new TaskResponse();
        completedResponse.setId(1L);
        completedResponse.setDescription("Test Task");
        completedResponse.setPriority(TaskPriority.MEDIA);
        completedResponse.setCompleted(true);
        completedResponse.setCreatedAt(LocalDateTime.now());
        completedResponse.setUpdatedAt(LocalDateTime.now());

        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.markTaskAsCompleted(1L, 1L)).thenReturn(completedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/1/complete")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldGetUserTasks() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.getUserPendingTasks(1L)).thenReturn(List.of(taskResponse));

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test Task"))
                .andExpect(jsonPath("$[0].priority").value(TaskPriority.MEDIA.getDisplayName()));
    }

    @Test
    void shouldGetUserTasksByPriority() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.getUserPendingTasksByPriority(1L, "ALTA")).thenReturn(List.of(taskResponse));

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer mockToken")
                .param("priority", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test Task"))
                .andExpect(jsonPath("$[0].priority").value(TaskPriority.MEDIA.getDisplayName()));
    }

    @Test
    void shouldReturnBadRequestWhenPriorityIsInvalid() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);

        // Act & Assert - Priority inválida deve retornar 400
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer mockToken")
                .param("priority", "INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Prioridade deve ser: ALTA, MEDIA ou BAIXA"));
    }

    @Test
    void shouldAcceptValidPriority() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("mockToken")).thenReturn(true);
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);
        when(taskService.getUserPendingTasksByPriority(1L, "ALTA")).thenReturn(List.of(taskResponse));

        // Act & Assert - Priority válida deve retornar 200
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer mockToken")
                .param("priority", "ALTA"))
                .andExpect(status().isOk());
    }
}