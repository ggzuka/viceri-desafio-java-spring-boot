package com.viceri.desafio.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viceri.desafio.todo.domain.dto.request.TaskCreateRequest;
import com.viceri.desafio.todo.domain.dto.response.TaskCreateResponse;
import com.viceri.desafio.todo.domain.security.CurrentUserId;
import com.viceri.desafio.todo.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskCreateResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            @CurrentUserId Long userId) {

        TaskCreateResponse response = taskService
                .createTask(userId, request.getDescription(), request.getPriority());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @CurrentUserId Long userId) {

        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

//  @GetMapping
//     public ResponseEntity<List<TaskResponse>> getUserTasks(
//             @CurrentUserId Long userId,
//             @RequestParam(required = false) String priority) {
        
//         List<TaskResponse> tasks;
//         if (priority != null) {
//             tasks = taskService.getUserTasksByPriority(userId, priority);
//         } else {
//             tasks = taskService.getUserTasks(userId);
//         }
        
//         return ResponseEntity.ok(tasks);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<TaskResponse> updateTask(
//             @PathVariable Long id,
//             @Valid @RequestBody TaskCreateRequest request,
//             @CurrentUserId Long userId) {
        
//         TaskResponse response = taskService.updateTask(id, request, userId);
//         return ResponseEntity.ok(response);
//     }

//     @PatchMapping("/{id}/complete")
//     public ResponseEntity<TaskResponse> markTaskAsCompleted(
//             @PathVariable Long id,
//             @CurrentUserId Long userId) {
        
//         TaskResponse response = taskService.markTaskAsCompleted(id, userId);
//         return ResponseEntity.ok(response);
//     }

}
