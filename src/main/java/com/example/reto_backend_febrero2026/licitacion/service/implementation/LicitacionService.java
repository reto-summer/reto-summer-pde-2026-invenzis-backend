package com.example.reto_backend_febrero2026.licitacion.service.implementation;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.mapper.FamiliaMapper;
import com.example.reto_backend_febrero2026.familia.repository.implementation.FamiliaRepository;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import com.example.reto_backend_febrero2026.licitacion.mapper.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.mapper.SubfamiliaMapper;
import com.example.reto_backend_febrero2026.subfamilia.repository.implementation.SubfamiliaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LicitacionService implements ILicitacionService {

    private final ILicitacionRepository tenderRepository;
    private final LicitacionMapper tenderMapper;
    private final FamiliaRepository familiaRepository;
    private final SubfamiliaRepository subfamiliaRepository;
    private final SubfamiliaMapper subfamiliaMapper;
    private final FamiliaMapper familiaMapper;

    public LicitacionService(ILicitacionRepository tenderRepository,
                             LicitacionMapper tenderMapper,
                             FamiliaRepository familiaRepository,
                             SubfamiliaRepository subfamiliaRepository, SubfamiliaMapper subfamiliaMapper, FamiliaMapper familiaMapper) {
        this.tenderRepository = tenderRepository;
        this.tenderMapper = tenderMapper;
        this.familiaRepository = familiaRepository;
        this.subfamiliaRepository = subfamiliaRepository;
        this.subfamiliaMapper = subfamiliaMapper;
        this.familiaMapper = familiaMapper;
    }

    @Override
    public LicitacionModelDTO getTenderById(int id) {
        LicitacionModel tender = tenderRepository.getTenderById(id);
        return tenderMapper.tenderToTenderDTO(tender);
    }

    @Override
    @Transactional
    public LicitacionModelDTO saveTender(LicitacionItemRecord dto) {
        Integer idExtraido = extraerIdDelLink(dto.link());

        if (idExtraido != null) {
            try {
                LicitacionModel existente = tenderRepository.getTenderById(idExtraido);
                if (existente != null) return tenderMapper.tenderToTenderDTO(existente);
            } catch (Exception ignored) {}
        }

        LicitacionModelDTO licitacionDto = new LicitacionModelDTO();
        licitacionDto.setIdLicitacion(idExtraido);
        licitacionDto.setTitle(dto.titulo());
        licitacionDto.setLink(dto.link());

        String descLimpia = limpiarHtml(dto.description());
        licitacionDto.setDescription(descLimpia);

        FamiliaModel familiaEntity;
        try {
            familiaEntity = familiaRepository.findById(dto.familaCod());
        } catch (Exception e) {
            familiaEntity = familiaRepository.save(new FamiliaModel(dto.familaCod(), "Familia " + dto.familaCod()));
        }
        licitacionDto.setFamilia(familiaMapper.familyToFamilyDTO(familiaEntity));

        SubfamiliaModel subfamiliaEntity;
        try {
            subfamiliaEntity = subfamiliaRepository.findById(dto.familaCod(), dto.subFamiliaCod());
        } catch (Exception e) {
            subfamiliaEntity = subfamiliaRepository.save(new SubfamiliaModel(dto.familaCod(), dto.subFamiliaCod(), "Subfamilia " + dto.subFamiliaCod()));
        }
        licitacionDto.setSubfamilia(subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaEntity));

        procesarFechas(dto, licitacionDto);

        LicitacionModel tender = tenderMapper.tenderDTOtoTender(licitacionDto);

        LicitacionModel guardado = tenderRepository.save(tender);
        return tenderMapper.tenderToTenderDTO(guardado);
    }

    private void procesarFechas(LicitacionItemRecord dto, LicitacionModelDTO licitacionDto) {
        // FechaPublicaciion
        try {
            if (dto.fechaPublicacion() != null) {
                licitacionDto.setFechaPublicacion(OffsetDateTime.parse(dto.fechaPublicacion(), DateTimeFormatter.RFC_1123_DATE_TIME));
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo fechaPublicacion: " + e.getMessage());
        }

        // FechaCierre
        if (dto.description() != null) {
            Pattern pFecha = Pattern.compile("hasta:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2})");
            Matcher mFecha = pFecha.matcher(dto.description());
            if (mFecha.find()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    licitacionDto.setFechaCierre(LocalDateTime.parse(mFecha.group(1), formatter));
                } catch (Exception e) {
                    System.err.println("Error obteniendo fechaCierre: " + e.getMessage());
                }
            }
        }
    }

    private String limpiarHtml(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("<br\\s*/?>", "\n")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&sol;", "/")
                .replaceAll("<.*?>", "")
                .trim();
    }

    private Integer extraerIdDelLink(String link) {
        if (link == null) return null;

        // Por si viene con formato JSON {link: "..."}
        String linkLimpio = link.replace("\"", "").replace("{", "").replace("}", "").trim();

        Pattern p = Pattern.compile("id/(\\d+)");
        Matcher m = p.matcher(linkLimpio);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    @Override
    public LicitacionModelDTO getTenderByTitle(String titulo) {
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
