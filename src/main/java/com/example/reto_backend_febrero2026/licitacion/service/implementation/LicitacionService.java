package com.example.reto_backend_febrero2026.licitacion.service.implementation;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.repository.implementation.FamiliaRepository;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import com.example.reto_backend_febrero2026.licitacion.mapper.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.repository.implementation.SubfamiliaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LicitacionService implements ILicitacionService {

    private final ILicitacionRepository tenderRepository;
    private final LicitacionMapper tenderMapper;
    private final FamiliaRepository familiaRepository;
    private final SubfamiliaRepository subfamiliaRepository;

    public LicitacionService(ILicitacionRepository tenderRepository,
                             LicitacionMapper tenderMapper, FamiliaRepository familiaRepository, SubfamiliaRepository subfamiliaRepository) {
        this.tenderRepository = tenderRepository;
        this.tenderMapper = tenderMapper;
        this.familiaRepository = familiaRepository;
        this.subfamiliaRepository = subfamiliaRepository;
    }

    @Override
    public LicitacionModelDTO getTenderById(int id) {
        LicitacionModel tender = tenderRepository.getTenderById(id);
        return tenderMapper.tenderToTenderDTO(tender);
    }

    @Override
    @Transactional
    public LicitacionModelDTO saveTender(LicitacionItemRecord dto) {
        LicitacionModelDTO licitacionDto = new LicitacionModelDTO();
        Integer idExtraido = null;

        if (dto.link() != null) {
            Pattern p = Pattern.compile("id/(\\d+)");
            Matcher m = p.matcher(dto.link());
            if (m.find()) {
                idExtraido = Integer.parseInt(m.group(1));
                licitacionDto.setIdLicitacion(idExtraido);
            }
        }

        try {
            if (idExtraido == null) throw new RuntimeException("ID no encontrado en link");

            LicitacionModel existente = tenderRepository.getTenderById(idExtraido);
            return tenderMapper.tenderToTenderDTO(existente);

        } catch (Exception e) {
            licitacionDto.setTitle(dto.titulo());
            licitacionDto.setDescription(dto.description());
            licitacionDto.setLink(dto.link());

            try {
                if (dto.fechaPublicacion() != null) {
                    licitacionDto.setFechaPublicacion(OffsetDateTime.parse(dto.fechaPublicacion(), DateTimeFormatter.RFC_1123_DATE_TIME));
                }
            } catch (Exception ex) {
                System.err.println("Error obteniendo la fecha publicación: " + ex.getMessage());
            }

            if (dto.description() != null) {
                Pattern pFecha = Pattern.compile("hasta:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2})");
                Matcher mFecha = pFecha.matcher(dto.description());
                if (mFecha.find()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    licitacionDto.setFechaCierre(LocalDateTime.parse(mFecha.group(1), formatter));
                }
            }

            try {
                licitacionDto.setFamilia(familiaRepository.findById(dto.familaCod()));
            } catch (Exception ex) {
                FamiliaModel familia = new FamiliaModel(dto.familaCod(), "Familia " + dto.familaCod());
                familiaRepository.save(familia);
                licitacionDto.setFamilia(familia);
            }

            try {
                licitacionDto.setSubfamilia(subfamiliaRepository.findById(dto.subFamiliaCod(), dto.familaCod()));
            } catch (Exception ex) {
                SubfamiliaModel subfamilia = new SubfamiliaModel(dto.familaCod(), dto.subFamiliaCod(), "Subfamilia " + dto.subFamiliaCod());
                subfamiliaRepository.save(subfamilia);
                licitacionDto.setSubfamilia(subfamilia);
            }

            LicitacionModel tender = tenderMapper.tenderDTOtoTender(licitacionDto);
            LicitacionModel guardado = tenderRepository.save(tender);

            return tenderMapper.tenderToTenderDTO(guardado);
        }
    }

    @Override
    public LicitacionModelDTO getTenderByTitle(String titulo){
        LicitacionModel tender = tenderRepository.getTenderByTitle(titulo);
        return tenderMapper.tenderToTenderDTO(tender);
    }

    /*
    @Override
    public LicitacionModelDTO getTenderByfecha_publicacion(LocalDate fecha){
        LicitacionModel tender = tenderRepository.getTenderByfecha_publicacion(fecha);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }
    @Override
    public LicitacionModelDTO getTenderByfecha_cierre(LocalDateTime fecha){
        LicitacionModel tender = tenderRepository.getTenderByfecha_cierre(fecha);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }

    @Override
    public LicitacionModelDTO getTenderByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin){
        LicitacionModel tender = tenderRepository.getTenderByfecha_inicio_fin(fecha_inicio, fecha_fin);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }
     */
}
