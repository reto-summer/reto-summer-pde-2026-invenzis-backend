package com.example.reto_backend_febrero2026.notificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {
    @Query("""
        SELECT n FROM Notificacion n
        ORDER BY n.fechaEjecucion DESC
    """)
    List<Notificacion> findAll();
    List<Notificacion> findByExitoTrue();
    List<Notificacion> findByExitoFalse();
}
