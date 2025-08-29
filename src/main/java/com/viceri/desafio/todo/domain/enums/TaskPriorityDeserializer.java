package com.viceri.desafio.todo.domain.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class TaskPriorityDeserializer extends JsonDeserializer<TaskPriority> {
    @Override
    public TaskPriority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        // Tenta pelo nome do enum
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
            if (priority.getDisplayName().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Prioridade inválida: " + value);
    }
}