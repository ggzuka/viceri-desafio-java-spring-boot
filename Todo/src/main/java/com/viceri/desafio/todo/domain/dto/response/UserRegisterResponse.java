package com.viceri.desafio.todo.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta retornada após registrar um usuário")
public class UserRegisterResponse {
    @Schema(description = "ID do usuário gerado pelo sistema", example = "1")

    private Long id;
    @Schema(description = "Nome do usuário", example = "Gabriel Guilhem")

    private String name;
    @Schema(description = "Email do usuário", example = "gabriel@example.com")

    private String email;

    // Construtores
    public UserRegisterResponse() {
    }

    public UserRegisterResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
