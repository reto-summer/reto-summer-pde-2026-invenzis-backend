package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;

@Service
public class LicitacionService implements ILicitacionService {

    private final LicitacionUtility licitacionUtility;
    private final ISubfamiliaService subfamiliaService;
    private final ILicitacionRepository licitacionRepository;
    private final LicitacionMapper licitacionMapper;

    public LicitacionService(LicitacionUtility licitacionUtility, ISubfamiliaService subfamiliaService,
                             ILicitacionRepository licitacionRepository, LicitacionMapper licitacionMapper) {
        this.licitacionUtility = licitacionUtility;
        this.subfamiliaService = subfamiliaService;
        this.licitacionRepository = licitacionRepository;
        this.licitacionMapper = licitacionMapper;
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

    @Transactional
    public LicitacionDTO save(LicitacionItemRecord itemRecord) {

        Integer id = licitacionUtility.extraerIdDelLink(itemRecord.link()).orElseThrow(() -> new IllegalArgumentException("No se pudo extraer ID del link"));

        LicitacionDTO dto = licitacionMapper.itemRecordToDTO(itemRecord);

        dto.setFamilia(iFamiliaService.findById(itemRecord.familiaCod()));
        dto.setSubfamilia(
                iSubfamiliaService.findById(
                        itemRecord.familiaCod(),
                        itemRecord.subFamiliaCod()
                )
        );

        Licitacion entity = licitacionMapper.licitacionDTOtoLicitacion(dto);
        entity.setIdLicitacion(id);

        Licitacion saved = licitacionRepository.save(entity);

        return licitacionMapper.licitacionToLicitacionDTO(saved);
    }

    private LicitacionDTO crearNuevaLicitacion(LicitacionItemRecord itemRecord) {

        if(itemRecord.familiaCod() == null || itemRecord.subFamiliaCod() == null) {
            throw new IllegalArgumentException("Familia o Subfamilia nulas para el item: " + itemRecord.link());
        }

        LicitacionDTO dto = licitacionMapper.itemRecordToDTO(itemRecord);

        SubfamiliaDTO subfamiliaDTO = subfamiliaService.findById(itemRecord.familiaCod(), itemRecord.subFamiliaCod());
        dto.setSubfamilias(List.of(subfamiliaDTO));

        Licitacion licitacion = licitacionRepository.save(
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
}
