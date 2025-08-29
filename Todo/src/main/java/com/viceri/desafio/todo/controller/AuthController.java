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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        AuthService.AuthResult authResult = authService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(authResult.getEmail(), authResult.getUserId());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
