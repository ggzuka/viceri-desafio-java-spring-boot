package com.viceri.desafio.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viceri.desafio.todo.domain.dto.request.UserLoginRequest;
import com.viceri.desafio.todo.domain.dto.response.AuthResponse;
import com.viceri.desafio.todo.domain.security.JwtUtil;
import com.viceri.desafio.todo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Realiza o login e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        AuthService.AuthResult authResult = authService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(authResult.getEmail(), authResult.getUserId());
        Long expirationMillis = jwtUtil.getExpiration();
        String expiresAt = java.time.Instant.ofEpochMilli(System.currentTimeMillis() + expirationMillis)
            .toString(); // formato ISO 8601

        return ResponseEntity.ok(new AuthResponse(token, expiresAt));
    }
}
