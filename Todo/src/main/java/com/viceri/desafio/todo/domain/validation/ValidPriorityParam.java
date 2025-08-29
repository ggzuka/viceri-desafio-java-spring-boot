package com.viceri.desafio.todo.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PriorityParamValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriorityParam {
    String message() default "Prioridade deve ser: ALTA, MEDIA ou BAIXA";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}