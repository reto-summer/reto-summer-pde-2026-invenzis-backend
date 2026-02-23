package com.example.reto_backend_febrero2026.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailRepository extends JpaRepository<MailDestination, Long> {

    Optional<MailDestination> findByEmail(String email);


    @Query("SELECT m.email FROM MailDestination m WHERE m.activo = true")
    List<String> findAllActiveEmails();

 
    List<MailDestination> findByActivoTrue();


    boolean existsByEmail(String email);
}
