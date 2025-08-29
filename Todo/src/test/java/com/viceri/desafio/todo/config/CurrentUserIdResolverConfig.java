package com.viceri.desafio.todo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;

@TestConfiguration
public class CurrentUserIdResolverConfig {

    @Bean
    public HandlerMethodArgumentResolver currentUserIdResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                // verifica se o parâmetro tem a anotação @CurrentUserId
                return parameter.hasParameterAnnotation(com.viceri.desafio.todo.domain.security.CurrentUserId.class)
                        && parameter.getParameterType().equals(Long.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                // sempre retorna o usuário "mockado" 1L
                return 1L;
            }
        };
    }
}
