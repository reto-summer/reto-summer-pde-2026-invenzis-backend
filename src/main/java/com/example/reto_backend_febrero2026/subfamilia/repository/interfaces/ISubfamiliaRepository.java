package com.example.reto_backend_febrero2026.subfamilia.repository.interfaces;

import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISubfamiliaRepository extends JpaRepository<Subfamilia, Subfamilia.SubfamiliaId> {

    List<Subfamilia> findByFamiCod(Integer famiCod);

}