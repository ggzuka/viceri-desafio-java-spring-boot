package com.viceri.desafio.todo.domain.dto.response;

public class UserRegisterResponse {
    private Long id;
    private String name;
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
