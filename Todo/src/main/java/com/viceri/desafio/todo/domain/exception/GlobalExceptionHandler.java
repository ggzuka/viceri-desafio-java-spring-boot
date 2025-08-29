package com.viceri.desafio.todo.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.viceri.desafio.todo.domain.enums.TaskPriority;

import jakarta.validation.ConstraintViolationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Prioridade inválida")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            body.put("message", ex.getMessage());
            body.put("validValues", Arrays.stream(TaskPriority.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // Para outras IllegalArgumentExceptions
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        // Extrair a mensagem de erro específica
        String errorMessage = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Parâmetro inválido");

        body.put("message", errorMessage);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handler genérico para exceções com @ResponseStatus
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Verifica se a exceção tem a anotação @ResponseStatus
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);

        if (responseStatus != null) {
            HttpStatus status = responseStatus.value();
            String message = ex.getMessage();

            // Para exceções com @ResponseStatus, retorna a mensagem no corpo
            Map<String, Object> body = new HashMap<>();
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message);

            return new ResponseEntity<>(body, status);
        }

        ex.printStackTrace();

        // Para outras exceções, retorna um erro genérico
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", "Erro interno do servidor");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
