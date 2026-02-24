package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.familia.*;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LicitacionService implements ILicitacionService {

    @Autowired
    LicitacionUtility licitacionUtility;

    @Autowired
    ISubfamiliaService iSubfamiliaService;

    @Autowired
    IFamiliaService iFamiliaService;

    @Autowired
    ILicitacionRepository licitacionRepository;

    @Autowired
    LicitacionMapper licitacionMapper;

    @Override
    public List<LicitacionDTO> findAll() {
        return licitacionRepository.findAll().stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LicitacionDTO getLicitacionById(int id) {
        return licitacionRepository
                .getLicitacionById(id)
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .orElseThrow(() -> new RuntimeException("No existe licitación con id: " + id));
    }

    @Auditable(module = "LICITACION_SERVICE", action = "CLEAN_SAVE")
    @Transactional
    public LicitacionDTO cleanSave(LicitacionItemRecord itemRecord){

        Integer id = licitacionUtility.extraerIdDelLink(itemRecord.link()).orElse(null);

        if (id != null) {
            Optional<Licitacion> existente = licitacionRepository.getLicitacionById(id);

            if (existente.isPresent()) {
                return licitacionMapper.licitacionToLicitacionDTO(existente.get());
            }
        }

        LicitacionDTO licitacionDTO = licitacionMapper.itemRecordToDTO(itemRecord);

        FamiliaDTO familiaDTO = iFamiliaService.findById(itemRecord.familiaCod());

        SubfamiliaDTO subfamiliaDTO = iSubfamiliaService.findById(itemRecord.familiaCod(), itemRecord.subFamiliaCod());

        licitacionDTO.setFamilia(familiaDTO);
        licitacionDTO.setSubfamilia(subfamiliaDTO);

        Licitacion licitacion = licitacionMapper.licitacionDTOtoLicitacion(licitacionDTO);
        licitacionRepository.save(licitacion);
        return licitacionDTO;
    }

    public LicitacionDTO getLicitacionByTitulo(String titulo){
        return licitacionRepository
                .getLicitacionByTitulo(titulo)
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .orElseThrow(() -> new RuntimeException("No existe licitación con titulo: " + titulo));
    }
}


