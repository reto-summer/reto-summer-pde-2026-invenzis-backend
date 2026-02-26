package com.example.reto_backend_febrero2026.subfamilia;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubfamiliaService implements ISubfamiliaService {

    private final ISubfamiliaRepository subfamiliaRepository;
    private final SubfamiliaMapper subfamiliaMapper;

    public SubfamiliaService(ISubfamiliaRepository subfamiliaRepository, SubfamiliaMapper subfamiliaMapper) {
        this.subfamiliaRepository = subfamiliaRepository;
        this.subfamiliaMapper = subfamiliaMapper;
    }

    @Override
    public List<SubfamiliaDTO> findAll() {
        return subfamiliaRepository.findAll()
                .stream()
                .map(subfamiliaMapper::subFamiliaToSubfamiliaDTO)
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

        return subfamiliaMapper.subFamiliaToSubfamiliaDTO(subfamilia);
    }

    @Override
    public List<SubfamiliaDTO> findByFamiCod(Integer famiCod) {
        return subfamiliaRepository.findByFamiCod(famiCod)
                .stream()
                .map(subfamiliaMapper::subFamiliaToSubfamiliaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubfamiliaDTO saveFamily(SubfamiliaDTO dto) {

        Subfamilia subfamilia = subfamiliaMapper.subFamiliaDTOtoSubfamilia(dto);

        if (subfamilia.getFamiCod() == null || subfamilia.getCod() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fami_cod y cod son obligatorios");
        }

        if (subfamilia.getDescripcion() == null) {
            subfamilia.setDescripcion("");
        }

        Subfamilia saved = subfamiliaRepository.save(subfamilia);

        return subfamiliaMapper.subFamiliaToSubfamiliaDTO(saved);
    }

    public SubfamiliaDTO getOrCreateSubFamily(Integer famCod, Integer subCod) {

        if (famCod == null || subCod == null) return null;

        Subfamilia subfamilia = subfamiliaRepository.findByFamiCodAndCod(famCod, subCod);


        if (subfamilia == null) {
            subfamilia = subfamiliaRepository.save(
                    new Subfamilia(famCod, subCod, "Subfamilia " + subCod)
            );
        }

        return subfamiliaMapper.subFamiliaToSubfamiliaDTO(subfamilia);
    }

    public Subfamilia getEntityById(Integer famiCod, Integer cod)
    {
        Subfamilia.SubfamiliaId id =
                new Subfamilia.SubfamiliaId(famiCod, cod);

        return this.subfamiliaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Subfamilia no encontrada"));
    }
}