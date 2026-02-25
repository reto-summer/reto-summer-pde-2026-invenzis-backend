package com.example.reto_backend_febrero2026.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IEmailRepository extends JpaRepository<Email, String> {

    @Query("SELECT m.emailAddress FROM Email m WHERE m.activo = true")
    List<String> findAllActiveEmails();

    List<Email> findByActivoTrue();
}
