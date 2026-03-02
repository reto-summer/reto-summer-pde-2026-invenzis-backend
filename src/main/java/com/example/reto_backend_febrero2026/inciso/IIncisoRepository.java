package com.example.reto_backend_febrero2026.inciso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IIncisoRepository extends JpaRepository<Inciso, Integer> {

    List<Inciso> findByNombreContainingIgnoreCase(String nombre);
}
