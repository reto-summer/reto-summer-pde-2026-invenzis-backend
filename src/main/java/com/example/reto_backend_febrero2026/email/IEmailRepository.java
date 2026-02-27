package com.example.reto_backend_febrero2026.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IEmailRepository extends JpaRepository<Email, String> {

    @Query("SELECT m.emailAddress FROM Email m WHERE m.activo = true")
    List<String> findAllActiveEmails();

    List<Email> findByActivoTrue();

    @Modifying
    @Transactional
    @Query("UPDATE Email e SET e.activo = :activo WHERE e.emailAddress = :email")
    void updateActivo(@Param("email") String email, @Param("activo") Boolean activo);
}
