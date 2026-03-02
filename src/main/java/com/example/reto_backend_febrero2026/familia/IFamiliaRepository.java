package com.example.reto_backend_febrero2026.familia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFamiliaRepository extends JpaRepository<Familia, Integer> {
}
