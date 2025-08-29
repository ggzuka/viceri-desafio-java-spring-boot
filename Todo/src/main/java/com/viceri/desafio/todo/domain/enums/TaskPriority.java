package com.viceri.desafio.todo.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Prioridade de uma tarefa", example = "ALTA")
public enum TaskPriority {
    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }
    
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public static TaskPriority fromDisplayName(String displayName) {
        for (TaskPriority priority : values()) {
            if (priority.displayName.equalsIgnoreCase(displayName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Prioridade desconhecida: " + displayName);
    }
}
