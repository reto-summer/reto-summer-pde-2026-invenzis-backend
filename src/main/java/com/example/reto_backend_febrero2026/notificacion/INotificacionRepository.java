package com.example.reto_backend_febrero2026.notificacion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByExitoTrue();
    List<Notificacion> findByExitoFalse();
}
