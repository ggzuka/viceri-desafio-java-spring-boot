package com.viceri.desafio.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.viceri.desafio.todo.domain.dto.request.TaskUpdateRequest;
import com.viceri.desafio.todo.domain.dto.response.TaskResponse;
import com.viceri.desafio.todo.domain.security.CurrentUserId;
import com.viceri.desafio.todo.domain.validation.ValidPriorityParam;
import com.viceri.desafio.todo.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@Validated
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
@SecurityRequirement(name = "bearerAuth") // Requer autenticação para todos os endpoints
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId) {

        TaskResponse response = taskService
                .createTask(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUserId Long userId) {

        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId) {

        TaskResponse response = taskService.updateTask(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markTaskAsCompleted(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUserId Long userId) {

        TaskResponse response = taskService.markTaskAsCompleted(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Lista as tarefas pendentes do usuário autenticado, opcionalmente filtradas por prioridade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "400", description = "Parâmetro de prioridade inválido")
    })
    public ResponseEntity<List<TaskResponse>> getUserTasks(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @RequestParam(required = false)
            @ValidPriorityParam() String priority) {

        List<TaskResponse> tasks;
        if (priority != null) {
            tasks = taskService.getUserPendingTasksByPriority(userId, priority);
        } else {
            tasks = taskService.getUserPendingTasks(userId);
        }

        return ResponseEntity.ok(tasks);
    }
}
