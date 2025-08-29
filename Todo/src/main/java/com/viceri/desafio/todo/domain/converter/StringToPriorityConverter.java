package com.viceri.desafio.todo.domain.converter;

import org.springframework.stereotype.Component;
import com.viceri.desafio.todo.domain.enums.TaskPriority;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToPriorityConverter implements Converter<String, TaskPriority> {

    @Override
    public TaskPriority convert(String source) {
        try {
            return TaskPriority.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Tenta converter pelo display name também
            for (TaskPriority priority : TaskPriority.values()) {
                if (priority.getDisplayName().equalsIgnoreCase(source)) {
                    return priority;
                }
            }
            throw new IllegalArgumentException("Prioridade inválida: " + source);
        }
    }
}