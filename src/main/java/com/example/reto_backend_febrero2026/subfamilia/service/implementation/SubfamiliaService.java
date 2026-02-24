package com.example.reto_backend_febrero2026.subfamilia.service.implementation;

import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaDTO;
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
    public List<SubfamiliaDTO> findAll() {
        return subfamiliaRepository.findAll()
                .stream()
                .map(subfamiliaMapper::subFamilyToSubfamilyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubfamiliaDTO findById(Integer famiCod, Integer cod) {

        Subfamilia.SubfamiliaId id =
                new Subfamilia.SubfamiliaId(famiCod, cod);

        Subfamilia subfamilia = subfamiliaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Subfamilia no encontrada con famiCod: "
                                + famiCod + " y cod: " + cod
                ));

        return subfamiliaMapper.subFamilyToSubfamilyDTO(subfamilia);
    }

    @Override
    public List<SubfamiliaDTO> findByFamiCod(Integer famiCod) {
        return subfamiliaRepository.findByFamiCod(famiCod)
                .stream()
                .map(subfamiliaMapper::subFamilyToSubfamilyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubfamiliaDTO saveFamily(SubfamiliaDTO dto) {

        Subfamilia subfamilia = subfamiliaMapper.subFamilyDTOtoSubfamily(dto);

        if (subfamilia.getFamiCod() == null || subfamilia.getCod() == null) {
            throw new IllegalArgumentException("FAMI_COD y COD son obligatorios");
        }

        if (subfamilia.getDescripcion() == null) {
            subfamilia.setDescripcion("");
        }

        Subfamilia saved = subfamiliaRepository.save(subfamilia);

        return subfamiliaMapper.subFamilyToSubfamilyDTO(saved);
    }
}