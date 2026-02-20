package com.example.reto_backend_febrero2026.familia;

import org.springframework.stereotype.Service;

import java.utils.List;

@Service
public class FamiliaService  implements IFamiliaService {
    private final FamiliaRepository familiaRepository;

    public FamiliaService(FamiliaRepository familiaRepository){
        this.familiaRepository = familiaRepository;
    }

    @Override
    public FamiliaModel findById(Integer cod) {
        return familiaRepository.findById(cod).orElseThrow(() -> new RunTimeException("Familia no encontrada por código: " + cod));
    }
}
