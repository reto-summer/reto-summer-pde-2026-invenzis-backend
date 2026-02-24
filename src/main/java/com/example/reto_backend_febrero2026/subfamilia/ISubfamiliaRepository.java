package com.example.reto_backend_febrero2026.subfamilia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubfamiliaRepository extends JpaRepository<Subfamilia, Subfamilia.SubfamiliaId> {

    List<Subfamilia> findByFamiCod(Integer famiCod);

    Subfamilia findByFamiCodAndCod(Integer famiCod, Integer cod);

}