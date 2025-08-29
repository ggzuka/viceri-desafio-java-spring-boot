package com.viceri.desafio.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.viceri.desafio.todo.domain.converter.StringToPriorityConverter;
import com.viceri.desafio.todo.domain.security.UserIdResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserIdResolver userIdResolver;
    private final StringToPriorityConverter stringToPriorityConverter;

    public WebConfig(UserIdResolver userIdResolver, StringToPriorityConverter stringToPriorityConverter) {
        this.stringToPriorityConverter = stringToPriorityConverter;
        this.userIdResolver = userIdResolver;
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdResolver);
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(stringToPriorityConverter);
    }
}