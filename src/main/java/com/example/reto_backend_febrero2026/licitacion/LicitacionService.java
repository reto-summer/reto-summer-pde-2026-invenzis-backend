package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.reto_backend_febrero2026.inciso.IIncisoService;
import com.example.reto_backend_febrero2026.inciso.IncisoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;

@Service
public class LicitacionService implements ILicitacionService {

    private final LicitacionUtility licitacionUtility;
    private final ISubfamiliaService iSubfamiliaService;
    private final IFamiliaService iFamiliaService;
    private final ILicitacionRepository licitacionRepository;
    private final LicitacionMapper licitacionMapper;
    private final IIncisoService incisoService;

    public LicitacionService(LicitacionUtility licitacionUtility, ISubfamiliaService subfamiliaService, IFamiliaService familiaService, ILicitacionRepository licitacionRepository, LicitacionMapper licitacionMapper, IIncisoService incisoService ) {
        this.licitacionUtility = licitacionUtility;
        this.iSubfamiliaService = subfamiliaService;
        this.iFamiliaService = familiaService;
        this.licitacionRepository = licitacionRepository;
        this.licitacionMapper = licitacionMapper;
        this.incisoService = incisoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> findByFilters(
            LocalDate fechaPublicacionDesde,
            LocalDate fechaPublicacionHasta,
            LocalDate fechaCierreDesde,
            LocalDate fechaCierreHasta,
            Integer familiaCod,
            Integer subfamiliaCod)
    {
        if (subfamiliaCod != null && familiaCod == null) {
            throw new IllegalArgumentException( "No se puede filtrar por subfamilia sin especificar la familia correspondiente.");
        }

        LocalDateTime cierreDesde = licitacionUtility.toStartOfDay(fechaCierreDesde);
        LocalDateTime cierreHasta = licitacionUtility.toEndOfDay(fechaCierreHasta);

        return licitacionRepository
                .findByFilters(fechaPublicacionDesde, fechaPublicacionHasta, cierreDesde, cierreHasta, familiaCod, subfamiliaCod)
                .stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LicitacionDTO getLicitacionById(int id) {
        if(id <= 0){
            throw new IllegalArgumentException("No puede ser cero ni negativo el id.");
        }
        return licitacionRepository
                .findById(id)
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .orElseThrow(() -> new EntityNotFoundException("No existe licitación con id: " + id));
    }

    @Auditable(module = "LICITACION_SERVICE", action = "CLEAN_SAVE")
    @Transactional
    public LicitacionDTO save(LicitacionItemRecord itemRecord){

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

        String nombreInciso = licitacionUtility.extraerNombreInciso(itemRecord.titulo());

        List<IncisoDTO> incisoDTO = incisoService.getByNombre(nombreInciso);

        licitacionDTO.setFamilia(familiaDTO);
        licitacionDTO.setSubfamilia(subfamiliaDTO);
        licitacionDTO.setInciso(incisoDTO.getFirst());

        Licitacion licitacion = licitacionMapper.licitacionDTOtoLicitacion(licitacionDTO);
        licitacionRepository.save(licitacion);

        return licitacionDTO;
    }

    //@Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> getLicitacionByTitulo(String titulo){
        return licitacionRepository
                .findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(Integer familiaCod, Integer subfamiliaCod) {

        if (subfamiliaCod != null && familiaCod == null) {
            throw new IllegalArgumentException( "No se puede filtrar por subfamilia sin especificar la familia correspondiente.");
        }

        List<Licitacion> licitaciones =
                licitacionRepository.findByFamilia_CodAndSubfamilia_Cod(familiaCod, subfamiliaCod);

        if (licitaciones.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen licitaciones para familiaCod=" + familiaCod +
                            " y subfamiliaCod=" + subfamiliaCod
            );
        }

        List<LicitacionDTO> resultado = licitaciones.stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .toList();

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicitacionDTO> getLicitacionesNoEnviadasByFamiliaAndSubfamilia(
            Integer familiaCod,
            Integer subfamiliaCod,
            List<String> emails) {

        if (subfamiliaCod != null && familiaCod == null) {
            throw new IllegalArgumentException( "No se puede filtrar por subfamilia sin especificar la familia correspondiente.");
        }

        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("La lista de emails no puede ser null o vacía");
        }

        List<Licitacion> licitaciones =
                licitacionRepository.findNoEnviadasByFamiliaAndSubfamiliaAndEmails(
                        familiaCod,
                        subfamiliaCod,
                        emails
                );

        if (licitaciones.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen licitaciones no enviadas para familiaCod=" + familiaCod +
                            ", subfamiliaCod=" + subfamiliaCod
            );
        }

        List<LicitacionDTO> resultado = licitaciones.stream()
                .map(licitacionMapper::licitacionToLicitacionDTO)
                .toList();

        return resultado;
    }
}
