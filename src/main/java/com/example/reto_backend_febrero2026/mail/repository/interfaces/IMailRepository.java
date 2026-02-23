package com.example.reto_backend_febrero2026.mail.repository.interfaces;

import com.example.reto_backend_febrero2026.mail.MailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IMailRepository extends JpaRepository<MailModel, Long> {

    Optional<MailModel> findByEmail(String email);

    @Query("SELECT m.email FROM MailModel m WHERE m.activo = true")
    List<String> findAllActiveEmails();

    List<MailModel> findByActivoTrue();

    boolean existsByEmail(String email);
}
