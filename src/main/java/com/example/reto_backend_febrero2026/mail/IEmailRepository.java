package com.example.reto_backend_febrero2026.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IEmailRepository extends JpaRepository<Email, Integer> {

    Optional<Email> findByEmail(String email);

    @Query("SELECT m.email FROM Email m WHERE m.activo = true")
    List<String> findAllActiveEmails();

    List<Email> findByActivoTrue();

    boolean existsByEmail(String email);
}
