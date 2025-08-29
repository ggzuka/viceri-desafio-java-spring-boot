package com.viceri.desafio.todo.domain.dto.request;

import com.viceri.desafio.todo.domain.enums.TaskPriority;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para alterar uma nova tarefa")
public class TaskUpdateRequest {

    @NotBlank(message = "Descrição é obrigatória")
    @Schema(description = "Descrição da tarefa", example = "Comprar materiais")
    private String description;

    @NotNull(message = "Prioridade é obrigatória")
    @Schema(description = "Prioridade da tarefa", example = "ALTA")
    private TaskPriority priority;

    // Getters e Setters
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
}