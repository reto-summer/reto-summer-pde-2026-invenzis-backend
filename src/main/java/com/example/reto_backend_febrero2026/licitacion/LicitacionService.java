package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.familia.*;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LicitacionService implements ILicitacionService {

    private static final Logger log = LoggerFactory.getLogger(LicitacionService.class);

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
    public LicitacionDTO getLicitacionById(int id) {
        Licitacion licitacion = licitacionRepository.getLicitacionById(id);
        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }

    @Auditable(module = "LICITACION_SERVICE", action = "CLEAN_SAVE")
    @Transactional
    public LicitacionDTO cleanSave(LicitacionItemRecord itemRecord){

        Integer id = licitacionUtility.extraerIdDelLink(itemRecord.link()).orElse(null);

        if (id != null) {
            Licitacion existente = licitacionRepository.getLicitacionById(id);
            if (existente != null) {
                return licitacionMapper.licitacionToLicitacionDTO(existente);
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
            Licitacion licitacion = licitacionRepository.getLicitacionByTitulo(titulo);
            LicitacionDTO dto = licitacionMapper.licitacionToLicitacionDTO(licitacion);
            return dto;
        }
    }


