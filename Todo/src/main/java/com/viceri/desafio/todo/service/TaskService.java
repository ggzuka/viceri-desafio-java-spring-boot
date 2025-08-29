package com.viceri.desafio.todo.service;

import com.viceri.desafio.todo.domain.dto.request.TaskUpdateRequest;
import com.viceri.desafio.todo.domain.dto.response.TaskCreateResponse;
import com.viceri.desafio.todo.domain.dto.response.TaskResponse;
import com.viceri.desafio.todo.domain.dto.response.TaskUpdateResponse;
import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.exception.shared.ResourceNotFoundException;
import com.viceri.desafio.todo.domain.model.Task;
import com.viceri.desafio.todo.repository.TaskRepository;
import com.viceri.desafio.todo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskCreateResponse createTask(Long userId, String description, TaskPriority priority) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Task newTask = new Task();
        newTask.setPriority(priority);
        newTask.setDescription(description);
        newTask.setUserId(userId);

        Task savedTask = taskRepository.save(newTask);
        return new TaskCreateResponse(savedTask);
    }

    public void deleteTask(Long id, Long userId) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Boolean deleted = taskRepository.delete(id, userId);
        if (!deleted) {
            throw new ResourceNotFoundException("Tarefa não encontrada ou não pertence ao usuário");
        }
    }

    public TaskUpdateResponse updateTask(Long id, TaskUpdateRequest request, Long userId) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return taskRepository
                .update(id, request.getDescription(), request.getPriority(), userId)
                .map(t -> new TaskUpdateResponse(t))
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou não pertence ao usuário"));
    }

    public TaskUpdateResponse markTaskAsCompleted(Long id, Long userId) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return taskRepository
                .markTaskAsCompleted(id, userId)
                .map(t -> new TaskUpdateResponse(t))
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou não pertence ao usuário"));
    }

    public List<TaskResponse> getUserPendingTasks(Long userId) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return taskRepository
                .findPendingByUserId(userId)
                .stream()
                .map(t -> new TaskResponse(t))
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getUserPendingTasksByPriority(Long userId, String priority) {

        // Verificar se o usuário existe
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return taskRepository
                .findPendingByUserIdAndPriority(userId, TaskPriority.valueOf(priority.toUpperCase()))
                .stream()
                .map(t -> new TaskResponse(t))
                .collect(Collectors.toList());
    }
}
