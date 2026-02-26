package com.example.reto_backend_febrero2026.licitacion_email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILicitacionEmailRepository extends JpaRepository<LicitacionEmail, LicitacionEmailId> {}
