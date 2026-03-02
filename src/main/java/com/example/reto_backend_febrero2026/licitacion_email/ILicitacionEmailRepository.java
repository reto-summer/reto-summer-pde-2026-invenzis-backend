package com.example.reto_backend_febrero2026.licitacion_email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILicitacionEmailRepository extends JpaRepository<LicitacionEmail, LicitacionEmailId> {

    List<LicitacionEmail> findByIdIdLicitacionIn(List<Integer> idLicitaciones);

    List<LicitacionEmail> findByEnviadoFalseAndIdEmailIn(List<String> emails);
}
