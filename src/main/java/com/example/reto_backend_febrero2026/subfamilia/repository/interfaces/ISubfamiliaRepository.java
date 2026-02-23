package com.example.reto_backend_febrero2026.subfamilia.repository.interfaces;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISubfamiliaRepository extends JpaRepository<SubfamiliaModel, SubfamiliaModel.SubfamiliaId> {

    List<SubfamiliaModel> findByFamiCod(Integer famiCod);

}