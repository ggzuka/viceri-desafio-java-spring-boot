package com.viceri.desafio.todo.domain.model;

import java.time.LocalDateTime;
import com.viceri.desafio.todo.domain.enums.TaskPriority;

public class Task {
    
    private Long id;
    private String description;
    private TaskPriority priority;
    private boolean completed;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtores
    public Task() {}

    public Task(String description, TaskPriority priority, Long userId) {
        this.description = description;
        this.priority = priority;
        this.userId = userId;
        this.completed = false;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { 
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
