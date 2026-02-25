package com.example.reto_backend_febrero2026.subfamilia;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
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
            throw new IllegalArgumentException("fami_cod y cod son obligatorios");
        }

        if (subfamilia.getDescripcion() == null) {
            subfamilia.setDescripcion("");
        }

        Subfamilia saved = subfamiliaRepository.save(subfamilia);

        return subfamiliaMapper.subFamilyToSubfamilyDTO(saved);
    }

    public SubfamiliaDTO getOrCreateSubFamily(Integer famCod, Integer subCod) {

        if (famCod == null || subCod == null) return null;

        Subfamilia subfamilia = subfamiliaRepository.findByFamiCodAndCod(famCod, subCod);


        if (subfamilia == null) {
            subfamilia = subfamiliaRepository.save(
                    new Subfamilia(famCod, subCod, "Subfamilia " + subCod)
            );
        }

        return subfamiliaMapper.subFamilyToSubfamilyDTO(subfamilia);
    }
}