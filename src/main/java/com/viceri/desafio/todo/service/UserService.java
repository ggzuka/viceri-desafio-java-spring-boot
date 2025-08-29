package com.viceri.desafio.todo.service;

import com.viceri.desafio.todo.domain.dto.request.UserRegisterRequest;
import com.viceri.desafio.todo.domain.dto.response.UserRegisterResponse;
import com.viceri.desafio.todo.domain.exception.user.DuplicateEmailException;
import com.viceri.desafio.todo.domain.exception.user.WeakPasswordException;
import com.viceri.desafio.todo.domain.model.User;
import com.viceri.desafio.todo.domain.validation.PasswordValidator;
import com.viceri.desafio.todo.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResponse registerUser(UserRegisterRequest request) {

        // Verificar se email já existe
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (!existingUser.isEmpty()) {
            throw new DuplicateEmailException("Email já cadastrado");
        }

        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new WeakPasswordException(
                    "Senha não corresponde aos requisitos mínimos de segurança. " +
                    "A senha deve conter letras maiúsculas, minúsculas, números e pelo menos 1 caractere especial EX:!@#$%&*");
        }

        String senhaCriptografada = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getName(), request.getEmail(), senhaCriptografada);
        User savedUser = userRepository.save(user);

        return new UserRegisterResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }

    public boolean login(String email, String senha) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(senha, user.getPassword()))
                .orElse(false);
    }
}
