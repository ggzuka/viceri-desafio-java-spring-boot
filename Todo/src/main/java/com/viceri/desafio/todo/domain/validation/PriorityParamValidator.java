package com.viceri.desafio.todo.domain.validation;

import com.viceri.desafio.todo.domain.enums.TaskPriority;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriorityParamValidator implements ConstraintValidator<ValidPriorityParam, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Parâmetro opcional
        }

        try {
            TaskPriority.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}