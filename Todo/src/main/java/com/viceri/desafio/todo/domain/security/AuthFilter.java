package com.viceri.desafio.todo.domain.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.viceri.desafio.todo.domain.exception.shared.UnauthorizedException;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public AuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);
                Long userId = jwtUtil.extractUserId(token);
                
                UserPrincipal userPrincipal = new UserPrincipal(userId, email);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UnauthorizedException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
    
    public static class UserPrincipal {
        private final Long userId;
        private final String email;
        
        public UserPrincipal(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getEmail() {
            return email;
        }
    }
}
