package com.example.reto_backend_febrero2026.subfamilia.service.implementation;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaModelDTO;
import com.example.reto_backend_febrero2026.subfamilia.mapper.SubfamiliaMapper;
import com.example.reto_backend_febrero2026.subfamilia.repository.interfaces.ISubfamiliaRepository;
import com.example.reto_backend_febrero2026.subfamilia.service.interfaces.ISubfamiliaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubfamiliaService implements ISubfamiliaService {

    private final ISubfamiliaRepository subfamiliaRepository;
    private final SubfamiliaMapper subfamiliaMapper;

    public SubfamiliaService(ISubfamiliaRepository subfamiliaRepository,
                             SubfamiliaMapper subfamiliaMapper) {
        this.subfamiliaRepository = subfamiliaRepository;
        this.subfamiliaMapper = subfamiliaMapper;
    }

    @Override
    public List<SubfamiliaModelDTO> findAll() {
        return subfamiliaRepository.findAll()
                .stream()
                .map(subfamiliaMapper::subFamilyToSubfamilyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubfamiliaModelDTO findById(Integer famiCod, Integer cod) {

        SubfamiliaModel.SubfamiliaId id =
                new SubfamiliaModel.SubfamiliaId(famiCod, cod);

        SubfamiliaModel subfamilia = subfamiliaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Subfamilia no encontrada con famiCod: "
                                + famiCod + " y cod: " + cod
                ));

        return subfamiliaMapper.subFamilyToSubfamilyDTO(subfamilia);
    }

    @Override
    public List<SubfamiliaModelDTO> findByFamiCod(Integer famiCod) {
        return subfamiliaRepository.findByFamiCod(famiCod)
                .stream()
                .map(subfamiliaMapper::subFamilyToSubfamilyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubfamiliaModelDTO saveFamily(SubfamiliaModelDTO dto) {

        SubfamiliaModel subfamilia =
                subfamiliaMapper.subFamilyDTOtoSubfamily(dto);

        SubfamiliaModel saved =
                subfamiliaRepository.save(subfamilia);

        return subfamiliaMapper.subFamilyToSubfamilyDTO(saved);
    }
}