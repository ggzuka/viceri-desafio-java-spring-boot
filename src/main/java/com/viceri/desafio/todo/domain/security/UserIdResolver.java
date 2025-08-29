package com.viceri.desafio.todo.domain.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.viceri.desafio.todo.domain.exception.shared.UnauthorizedException;

@Component
public class UserIdResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    public UserIdResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) &&
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new UnauthorizedException("Não foi possível obter a requisição");
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token não fornecido");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Token inválido");
        }

        return jwtUtil.extractUserId(token);
    }
}
