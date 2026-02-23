package com.example.reto_backend_febrero2026.familia.service.implementation;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;
import com.example.reto_backend_febrero2026.familia.mapper.FamiliaMapper;
import com.example.reto_backend_febrero2026.familia.repository.interfaces.IFamiliaRepository;
import com.example.reto_backend_febrero2026.familia.service.interfaces.IFamiliaService;
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
    public List<FamiliaModelDTO> findAll() {
        return familiaRepository.findAll()
                .stream()
                .map(familiaMapper::familyToFamilyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FamiliaModelDTO findById(Integer cod) {
        FamiliaModel familia = familiaRepository.findById(cod);
        return familiaMapper.familyToFamilyDTO(familia);
    }

    @Override
    public FamiliaModelDTO saveFamily(FamiliaModelDTO dto) {

        FamiliaModel familia = familiaMapper.familyDTOtoFamily(dto);

        FamiliaModel saved = familiaRepository.save(familia);

        return familiaMapper.familyToFamilyDTO(saved);
    }
}