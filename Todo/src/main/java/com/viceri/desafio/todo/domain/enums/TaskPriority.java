package com.viceri.desafio.todo.domain.enums;

public enum TaskPriority {
    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

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
