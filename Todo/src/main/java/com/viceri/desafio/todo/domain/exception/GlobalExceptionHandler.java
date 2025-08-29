package com.viceri.desafio.todo.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
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
        
        // Para outras exceções, retorna um erro genérico
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", "Erro interno do servidor");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
