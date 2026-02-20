package com.example.reto_backend_febrero2026.subfamilia;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubfamiliaRepository extends JpaRepository<SubfamiliaModel, SubfamiliaModel.SubfamiliaId> {

    List<SubfamiliaModel> findByFamiCod(Integer famiCod);

}