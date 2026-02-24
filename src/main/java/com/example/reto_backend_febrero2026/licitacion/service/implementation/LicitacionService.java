package com.example.reto_backend_febrero2026.licitacion.service.implementation;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.familia.mapper.FamiliaMapper;
import com.example.reto_backend_febrero2026.familia.repository.implementation.FamiliaRepository;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.mapper.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.mapper.SubfamiliaMapper;
import com.example.reto_backend_febrero2026.subfamilia.repository.implementation.SubfamiliaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LicitacionService implements ILicitacionService {

    private static final Logger log = LoggerFactory.getLogger(LicitacionService.class);

    private final ILicitacionRepository licitacionRepository;
    private final LicitacionMapper licitacionMapper;
    private final FamiliaRepository familiaRepository;
    private final SubfamiliaRepository subfamiliaRepository;
    private final SubfamiliaMapper subfamiliaMapper;
    private final FamiliaMapper familiaMapper;

    public LicitacionService(ILicitacionRepository licitacionRepository, LicitacionMapper licitacionMapper, FamiliaRepository familiaRepository,
                             SubfamiliaRepository subfamiliaRepository, SubfamiliaMapper subfamiliaMapper, FamiliaMapper familiaMapper) {
        this.licitacionRepository = licitacionRepository;
        this.licitacionMapper = licitacionMapper;
        this.familiaRepository = familiaRepository;
        this.subfamiliaRepository = subfamiliaRepository;
        this.subfamiliaMapper = subfamiliaMapper;
        this.familiaMapper = familiaMapper;
    }

    @Override
    public LicitacionDTO getLicitacionById(int id) {
        Licitacion licitacion = licitacionRepository.getLicitacionById(id);
        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }

    @Override
    @Transactional
    public LicitacionDTO saveLicitacion(LicitacionItemRecord dto) {

        Integer idExtraido = extraerIdDelLink(dto.link());

        if (idExtraido != null) {
            Licitacion existente = licitacionRepository.getLicitacionById(idExtraido);
            if (existente != null) {
                return licitacionMapper.licitacionToLicitacionDTO(existente);
            }
        }

        LicitacionDTO licitacionDto = new LicitacionDTO();
        licitacionDto.setIdLicitacion(idExtraido);
        licitacionDto.setTitulo(dto.titulo());
        licitacionDto.setLink(dto.link());
        licitacionDto.setDescripcion(limpiarHtml(dto.descripcion()));

        Familia familiaEntity = familiaRepository.findById(dto.familiaCod());
        if (familiaEntity == null) {
            familiaEntity = familiaRepository.save(
                    new Familia(dto.familiaCod(), "Familia " + dto.familiaCod())
            );
        }
        licitacionDto.setFamilia(familiaMapper.familyToFamilyDTO(familiaEntity));

        Subfamilia subfamiliaEntity =
                subfamiliaRepository.findById(dto.familiaCod(), dto.subFamiliaCod());

        if (subfamiliaEntity == null) {
            subfamiliaEntity = subfamiliaRepository.save(
                    new Subfamilia(
                            dto.familiaCod(),
                            dto.subFamiliaCod(),
                            "Subfamilia " + dto.subFamiliaCod()
                    )
            );
        }
        licitacionDto.setSubfamilia(
                subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaEntity)
        );

        procesarFechas(dto, licitacionDto);

        Licitacion licitacion = licitacionMapper.licitacionDTOtoLicitacion(licitacionDto);
        Licitacion guardado = licitacionRepository.save(licitacion);

        return licitacionMapper.licitacionToLicitacionDTO(guardado);
    }

    private void procesarFechas(LicitacionItemRecord dto, LicitacionDTO licitacionDto) {

        if (dto.fechaPublicacion() != null) {
            try {
                OffsetDateTime odt = OffsetDateTime.parse(
                        dto.fechaPublicacion(),
                        DateTimeFormatter.RFC_1123_DATE_TIME
                );

                OffsetDateTime horaUruguay =
                        odt.withOffsetSameInstant(ZoneOffset.of("-03:00"));

                licitacionDto.setFechaPublicacion(horaUruguay);

            } catch (Exception e) {
                log.warn("Error parseando fechaPublicacion: {}", e.getMessage());
            }
        }

        if (dto.descripcion() != null) {

            Pattern pFecha = Pattern.compile(
                    "hasta:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2})"
            );

            Matcher mFecha = pFecha.matcher(dto.descripcion());

            if (mFecha.find()) {
                try {
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                    licitacionDto.setFechaCierre(
                            LocalDateTime.parse(mFecha.group(1), formatter)
                    );

                } catch (Exception e) {
                    log.warn("Error parseando fechaCierre: {}", e.getMessage());
                }
            }
        }
    }

    private String limpiarHtml(String texto) {
        if (texto == null) return "";

        return texto
                .replaceAll("<br\\s*/?>", "\n")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&sol;", "/")
                .replaceAll("<.*?>", "")
                .trim();
    }

    private Integer extraerIdDelLink(String link) {
        if (link == null) return null;

        String linkLimpio = link
                .replace("\"", "")
                .replace("{", "")
                .replace("}", "")
                .trim();

        Pattern p = Pattern.compile("id/(\\d+)");
        Matcher m = p.matcher(linkLimpio);

        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    @Override
    public LicitacionDTO getLicitacionByTitulo(String titulo) {
        Licitacion licitacion =
                licitacionRepository.getLicitacionByTitulo(titulo);

        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }
}

    /*
    @Override
    public LicitacionModelDTO getlicitacionByfecha_publicacion(LocalDate fecha){
        LicitacionModel licitacion = licitacionRepository.getlicitacionByfecha_publicacion(fecha);
        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }
    @Override
    public LicitacionModelDTO getlicitacionByfecha_cierre(LocalDateTime fecha){
        LicitacionModel licitacion = licitacionRepository.getlicitacionByfecha_cierre(fecha);
        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }

    @Override
    public LicitacionModelDTO getlicitacionByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin){
        LicitacionModel licitacion = licitacionRepository.getlicitacionByfecha_inicio_fin(fecha_inicio, fecha_fin);
        return licitacionMapper.licitacionToLicitacionDTO(licitacion);
    }
 }
     */

