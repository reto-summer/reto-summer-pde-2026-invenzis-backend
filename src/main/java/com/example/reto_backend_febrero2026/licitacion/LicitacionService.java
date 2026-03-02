package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.reto_backend_febrero2026.email.EmailMapper;
import com.example.reto_backend_febrero2026.email.IEmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;

@Service
public class LicitacionService implements ILicitacionService {

    private final LicitacionUtility licitacionUtility;
    private final ISubfamiliaService iSubfamiliaService;
    private final IFamiliaService iFamiliaService;
    private final ILicitacionRepository licitacionRepository;
    private final LicitacionMapper licitacionMapper;
    private final IEmailService emailService;
    private final EmailMapper emailMapper;

    public LicitacionService(LicitacionUtility licitacionUtility, ISubfamiliaService subfamiliaService, IFamiliaService familiaService,
                             ILicitacionRepository licitacionRepository, LicitacionMapper licitacionMapper,
                             IEmailService emailService, EmailMapper emailMapper) {
        this.licitacionUtility = licitacionUtility;
        this.iSubfamiliaService = subfamiliaService;
        this.iFamiliaService = familiaService;
        this.licitacionRepository = licitacionRepository;
        this.licitacionMapper = licitacionMapper;
        this.emailService = emailService;
        this.emailMapper = emailMapper;

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
    public LicitacionDTO save(LicitacionItemRecord itemRecord) {

        Integer id = licitacionUtility
                .extraerIdDelLink(itemRecord.link())
                .orElse(null);

        if (id != null) {
            return licitacionRepository.findById(id)
                    .map(licitacionMapper::licitacionToLicitacionDTO)
                    .orElseGet(() -> crearNuevaLicitacion(itemRecord));
        }

        return crearNuevaLicitacion(itemRecord);
    }

    private LicitacionDTO crearNuevaLicitacion(LicitacionItemRecord itemRecord) {

        LicitacionDTO dto = licitacionMapper.itemRecordToDTO(itemRecord);

        dto.setFamilia(iFamiliaService.findById(itemRecord.familiaCod()));
        dto.setSubfamilia(
                iSubfamiliaService.findById(
                        itemRecord.familiaCod(),
                        itemRecord.subFamiliaCod()
                )
        );

        Licitacion licitacion =
                licitacionRepository.save(
                        licitacionMapper.licitacionDTOtoLicitacion(dto)
                );

        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
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


    // SIN SENTIDO, USAR EL FINDBYFILTERS
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

}
