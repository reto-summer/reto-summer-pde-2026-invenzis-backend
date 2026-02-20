package com.example.reto_backend_febrero2026.familia;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamiliaService implements IFamiliaService {
    private final FamiliaRepository familiaRepository;

    public FamiliaService(FamiliaRepository familiaRepository){
        this.familiaRepository = familiaRepository;
    }

    @Override
    public List<FamiliaModel> findAll() {
        return familiaRepository.findAll();
    }

    @Override
    public FamiliaModel findById(Integer cod) {
        return familiaRepository.findById(cod).orElseThrow(() -> new RuntimeException("Familia no encontrada por código: " + cod));
    }
}
