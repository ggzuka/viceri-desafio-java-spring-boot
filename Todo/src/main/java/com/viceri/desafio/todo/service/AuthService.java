package com.viceri.desafio.todo.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viceri.desafio.todo.domain.exception.shared.UnauthorizedException;
import com.viceri.desafio.todo.domain.model.User;
import com.viceri.desafio.todo.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResult authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        var login = user.map(u -> passwordEncoder.matches(password, u.getPassword())).orElse(false);
        
        if (!login) {
            throw new UnauthorizedException("Credenciais inválidas");
        }
        
        return new AuthResult(user.get());
    }

    public static class AuthResult {
        private final String email;
        private final Long userId;

        public AuthResult(String email, Long userId) {
            this.email = email;
            this.userId = userId;
        }

        public AuthResult(User user) {
            this.email = user.getEmail();
            this.userId = user.getId();
        }

        public String getEmail() {
            return email;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
