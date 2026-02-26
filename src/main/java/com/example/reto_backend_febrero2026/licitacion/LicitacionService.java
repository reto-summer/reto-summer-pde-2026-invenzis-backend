package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;

@Service
public class LicitacionService implements ILicitacionService {

    @Autowired
    private LicitacionUtility licitacionUtility;

    @Autowired
    private ISubfamiliaService iSubfamiliaService;

    @Autowired
    private IFamiliaService iFamiliaService;

    @Autowired
    private ILicitacionRepository licitacionRepository;

    @Autowired
    private LicitacionMapper licitacionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> findAll(
            LocalDate fechaPublicacionDesde,
            LocalDate fechaPublicacionHasta,
            LocalDate fechaCierreDesde,
            LocalDate fechaCierreHasta,
            Integer familiaCod,
            Integer subfamiliaCod)
    {
        if (subfamiliaCod != null && familiaCod == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede filtrar por subfamilia sin especificar la familia correspondiente."
            );
        }

        LocalDateTime cierreDesde = licitacionUtility.toStartOfDay(fechaCierreDesde);
        LocalDateTime cierreHasta = licitacionUtility.toEndOfDay(fechaCierreHasta);

        return licitacionRepository
                .getLicitacionesByFechas(fechaPublicacionDesde, fechaPublicacionHasta, cierreDesde, cierreHasta, familiaCod, subfamiliaCod)
                .stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LicitacionDTO getLicitacionById(int id) {
        return licitacionRepository
                .findById(id)
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe licitación con id: " + id
                ));
    }

    @Auditable(module = "LICITACION_SERVICE", action = "CLEAN_SAVE")
    @Transactional
    public LicitacionDTO cleanSave(LicitacionItemRecord itemRecord){

        Integer id = licitacionUtility.extraerIdDelLink(itemRecord.link()).orElse(null);

        if (id != null) {
            Optional<Licitacion> existente = licitacionRepository.findById(id);

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

    @Transactional(readOnly = true)
    public LicitacionDTO getLicitacionByTitulo(String titulo){
        return licitacionRepository
                .getLicitacionByTitulo(titulo)
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe licitación con titulo: " + titulo
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(Integer familiaCod, Integer subfamiliaCod) {
        List<Licitacion> licitaciones = licitacionRepository.findByFamilia_CodAndSubfamilia_Cod(familiaCod,subfamiliaCod);

        if(licitaciones.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen licitaciones para familia " + familiaCod
            + " y subfamilia " + subfamiliaCod);
        }

        return licitaciones.stream()
                .map(licitacionMapper::licitacionToLicitacionDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> getLicitacionesNoEnviadasByFamiliaAndSubfamilia(Integer familiaCod, Integer subfamiliaCod) {
        return licitacionRepository.findByFamilia_CodAndSubfamilia_CodAndEnviadoFalse(familiaCod, subfamiliaCod).stream()
                .map(licitacionMapper::licitacionToLicitacionDTO).collect(Collectors.toList());
    }

    @Transactional
    public LicitacionDTO updateEnviadoFlag(Integer id, boolean flag) {

        Licitacion licitacion = licitacionRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe licitación con id: " + id
                        )
                );
        licitacion.setEnviado(flag);

        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }
}


