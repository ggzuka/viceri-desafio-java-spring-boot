package com.viceri.desafio.todo.service;

import com.viceri.desafio.todo.domain.dto.request.TaskCreateRequest;
import com.viceri.desafio.todo.domain.dto.request.TaskUpdateRequest;
import com.viceri.desafio.todo.domain.dto.response.TaskResponse;
import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.exception.shared.ResourceNotFoundException;
import com.viceri.desafio.todo.domain.model.Task;
import com.viceri.desafio.todo.domain.model.User;
import com.viceri.desafio.todo.repository.TaskRepository;
import com.viceri.desafio.todo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private TaskCreateRequest taskCreateRequest;
    private TaskUpdateRequest taskUpdateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        task = new Task();
        task.setId(1L);
        task.setPriority(TaskPriority.MEDIA);
        task.setDescription("Tarefa de Testes");
        task.setUserId(1L);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setDescription(task.getDescription());
        taskCreateRequest.setPriority(task.getPriority());

        taskUpdateRequest = new TaskUpdateRequest();
        taskUpdateRequest.setDescription("Nova descrição da tarefa");
        taskUpdateRequest.setPriority(TaskPriority.ALTA);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task newTask = invocation.getArgument(0);
            newTask.setId(1L);
            newTask.setCreatedAt(LocalDateTime.now());
            return newTask;
        });

        // Act
        TaskResponse response = taskService.createTask(taskCreateRequest, userId);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(taskCreateRequest.getDescription(), response.getDescription());
        assertEquals(taskCreateRequest.getPriority(), response.getPriority());
        assertNotNull(response.getCreatedAt());
        verify(taskRepository).save(any(Task.class));
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForTaskCreation() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskCreateRequest, userId));
        verify(taskRepository, never()).save(any(Task.class));
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.delete(taskId, userId)).thenReturn(true);

        // Act
        taskService.deleteTask(taskId, userId);

        // Assert
        verify(taskRepository).delete(taskId, userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForDeletion() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(taskId, userId));
        verify(taskRepository, never()).delete(anyLong(), anyLong());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForDeletion() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.delete(taskId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(taskId, userId));
        verify(taskRepository).delete(taskId, userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long taskId = 1L;
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setDescription(taskUpdateRequest.getDescription());
        updatedTask.setPriority(taskUpdateRequest.getPriority());
        updatedTask.setUserId(userId);
        updatedTask.setCreatedAt(LocalDateTime.now());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.update(taskId, taskUpdateRequest.getDescription(), taskUpdateRequest.getPriority(), userId))
                .thenReturn(Optional.of(updatedTask));

        // Act
        TaskResponse response = taskService.updateTask(taskId, taskUpdateRequest, userId);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals(taskUpdateRequest.getDescription(), response.getDescription());
        assertEquals(taskUpdateRequest.getPriority(), response.getPriority());
        verify(taskRepository).update(taskId, taskUpdateRequest.getDescription(), taskUpdateRequest.getPriority(),
                userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForUpdate() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateRequest, userId));
        verify(taskRepository, never()).update(anyLong(), anyString(), any(), anyLong());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.update(taskId, taskUpdateRequest.getDescription(), taskUpdateRequest.getPriority(), userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateRequest, userId));
        verify(taskRepository).update(taskId, taskUpdateRequest.getDescription(), taskUpdateRequest.getPriority(),
                userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldMarkTaskAsCompleted() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;
        Task completedTask = new Task();
        completedTask.setId(taskId);
        completedTask.setDescription("Tarefa de Testes");
        completedTask.setPriority(TaskPriority.MEDIA);
        completedTask.setUserId(userId);
        completedTask.setCompleted(true);
        completedTask.setCreatedAt(LocalDateTime.now());
        completedTask.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.markTaskAsCompleted(taskId, userId))
                .thenReturn(Optional.of(completedTask));

        // Act
        TaskResponse response = taskService.markTaskAsCompleted(taskId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertTrue(response.isCompleted());
        verify(taskRepository).markTaskAsCompleted(taskId, userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForCompletion() {
        // Arrange
        Long taskId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.markTaskAsCompleted(taskId, userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.markTaskAsCompleted(taskId, userId));
        verify(taskRepository).markTaskAsCompleted(taskId, userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldGetUserPendingTasks() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findPendingByUserId(userId)).thenReturn(List.of(task));

        // Act
        List<TaskResponse> result = taskService.getUserPendingTasks(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(taskRepository).findPendingByUserId(userId);
    }

    @Test
    void shouldGetUserPendingTasksByPriority() {
        // Arrange
        Long userId = 1L;
        String priority = "ALTA";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findPendingByUserIdAndPriority(userId, TaskPriority.ALTA))
                .thenReturn(List.of(task));

        // Act
        List<TaskResponse> result = taskService.getUserPendingTasksByPriority(userId, priority);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(taskRepository).findPendingByUserIdAndPriority(userId, TaskPriority.ALTA);
    }

}