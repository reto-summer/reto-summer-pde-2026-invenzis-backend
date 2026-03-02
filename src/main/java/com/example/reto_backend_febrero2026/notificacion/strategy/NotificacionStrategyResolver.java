package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

@Component
public class NotificacionStrategyResolver {

    private final Map<NotificacionType, INotificacionStrategy> strategyByType;

    public NotificacionStrategyResolver(List<INotificacionStrategy> strategies) {
        this.strategyByType = strategies.stream()
                .collect(Collectors.toMap(INotificacionStrategy::getNotificacionType, Function.identity()));
    }

    public INotificacionStrategy resolve(NotificacionType type) {
        INotificacionStrategy strategy = strategyByType.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No existe estrategia para el tipo de notificación: " + type);
        }
        return strategy;
    }
}