package com.example.reto_backend_febrero2026.inciso;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncisoService implements IIncisoService {

    private final IIncisoRepository incisoRepository;
    private final IncisoMapper incisoMapper;

    public IncisoService(IIncisoRepository incisoRepository, IncisoMapper incisoMapper){
        this.incisoRepository = incisoRepository;
        this.incisoMapper = incisoMapper;
    }

    public List<IncisoDTO> getAll() {
        return this.incisoRepository.findAll()
                .stream()
                .map(incisoMapper::incisoToIncisoDTO)
                .toList();
    }

    public IncisoDTO getById(Integer id){
        return this.incisoRepository.findById(id)
                .map(incisoMapper::incisoToIncisoDTO)
                .orElseThrow(() -> new EntityNotFoundException("Inciso no encontrado"));
    }

    public List<IncisoDTO> getByNombre(String nombre){

        if (nombre.length() < 3) {
            throw new IllegalArgumentException("Debe ingresar al menos 3 caracteres");
        }

        return this.incisoRepository.findByNombreContainingIgnoreCase(nombre.trim())
                .stream()
                .map(incisoMapper::incisoToIncisoDTO)
                .toList();
    }
}
