package com.viceri.desafio.todo.domain.dto.request;

import com.viceri.desafio.todo.domain.enums.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskUpdateRequest {
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Prioridade é obrigatória")
    private TaskPriority priority;

    // Getters e Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
}