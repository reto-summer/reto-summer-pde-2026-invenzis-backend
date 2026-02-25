package com.example.reto_backend_febrero2026.familia;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FamiliaService implements IFamiliaService {

    private final IFamiliaRepository familiaRepository;
    private final FamiliaMapper familiaMapper;

    public FamiliaService(IFamiliaRepository familiaRepository,
                          FamiliaMapper familiaMapper) {
        this.familiaRepository = familiaRepository;
        this.familiaMapper = familiaMapper;
    }

    @Override
    public List<FamiliaDTO> findAll() {
        return familiaRepository.findAll()
                .stream()
                .map(familiaMapper::familyToFamilyDTO)
                .toList();
    }

    @Override
    public FamiliaDTO findById(Integer cod) {
        return familiaRepository.findById(cod)
                .map(familiaMapper::familyToFamilyDTO)
                .orElseThrow(() ->
                        new IllegalArgumentException("No se encontró la familia con cod=" + cod)
                );
    }

    @Override
    public FamiliaDTO saveFamily(FamiliaDTO dto) {

        if (dto.getCod() == null) {
            throw new IllegalArgumentException("cod no puede ser null");
        }

        if (dto.getDescripcion() == null) {
            dto.setDescripcion("");
        }

        Familia familia = familiaMapper.familyDTOtoFamily(dto);

        Familia saved = familiaRepository.save(familia);

        FamiliaDTO savedDTO = familiaMapper.familyToFamilyDTO(saved);

        return savedDTO;
    }

    public Familia getEntityById(Integer cod)
    {
        return this.familiaRepository.findById(cod).orElseThrow(() -> new EntityNotFoundException("Familia no encontrada"));
    }

    private Familia getOrCreateFamily(Integer cod) {

        if (cod == null) {
            return null;
        }

        Optional<Familia> existente = familiaRepository.findById(cod);

        if (existente.isPresent()) {
            return existente.get();
        }

        Familia nuevaFamilia = new Familia(cod, "Familia " + cod);

        return familiaRepository.save(nuevaFamilia);
    }
}