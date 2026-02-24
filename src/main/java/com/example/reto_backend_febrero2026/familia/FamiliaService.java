package com.example.reto_backend_febrero2026.familia;

import org.springframework.stereotype.Service;

import java.util.List;
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
                .collect(Collectors.toList());
    }

    @Override
    public FamiliaDTO findById(Integer cod) {
        try {
            Familia familia = familiaRepository.findById(cod);
            return familiaMapper.familyToFamilyDTO(familia);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("No se encontró la familia con COD=" + cod, e);
        }
    }

    @Override
    public FamiliaDTO saveFamily(FamiliaDTO dto) {

        Familia familia = familiaMapper.familyDTOtoFamily(dto);

        if (familia.getCod() == null) {
            throw new IllegalArgumentException("COD no puede ser null");
        }

        if (familia.getDescripcion() == null) {
            familia.setDescripcion("");
        }

        Familia saved = familiaRepository.save(familia);

        return familiaMapper.familyToFamilyDTO(saved);
    }

    private Familia getOrCreateFamily(Integer cod) {
        if (cod == null) return null;
        Familia f = familiaRepository.findById(cod);
        return (f != null) ? f : familiaRepository.save(new Familia(cod, "Familia " + cod));
    }
}