package com.viceri.desafio.todo.domain.dto.response;

import java.time.LocalDateTime;

import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.model.Task;

public class TaskCreateResponse {
    private Long id;
    private String description;
    private TaskPriority priority;
    private LocalDateTime createdAt;

    public TaskCreateResponse(
            Task savedTask) {
        this.id = savedTask.getId();
        this.description = savedTask.getDescription();
        this.priority = savedTask.getPriority();
        this.createdAt = savedTask.getCreatedAt();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}