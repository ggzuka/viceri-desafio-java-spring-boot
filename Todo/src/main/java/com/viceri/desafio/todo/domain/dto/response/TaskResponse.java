package com.viceri.desafio.todo.domain.dto.response;

import java.time.LocalDateTime;

import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.model.Task;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com informações de uma tarefa")
public class TaskResponse {
    @Schema(description = "ID da tarefa", example = "1")

    private Long id;
    @Schema(description = "Descrição da tarefa", example = "Comprar materiais")

    private String description;
    @Schema(description = "Prioridade da tarefa", example = "ALTA")

    private TaskPriority priority;
    @Schema(description = "Status de conclusão da tarefa", example = "false")

    private boolean completed;
    @Schema(description = "Data de criação da tarefa", example = "2025-08-29T15:00:00")

    private LocalDateTime createdAt;
    @Schema(description = "Data da última atualização da tarefa", example = "2025-08-29T15:30:00")

    private LocalDateTime updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.priority = task.getPriority();
        this.completed = task.isCompleted();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}