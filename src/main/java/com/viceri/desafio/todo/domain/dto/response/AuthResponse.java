package com.viceri.desafio.todo.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT")

public class AuthResponse {
    @Schema(description = "Token JWT gerado após login", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Data de expiração do token em formato ISO 8601", example = "2025-08-29T15:30:00Z")
    private String expiresAt;

    public AuthResponse() {}

    public AuthResponse(String token, String expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}