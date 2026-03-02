package com.example.reto_backend_febrero2026.licitacion;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LicitacionUtility {

    private static final DateTimeFormatter FORMATTER_CIERRE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern PATTERN_HASTA = Pattern.compile("hasta:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2})");
    private static final Pattern PATTERN_ID = Pattern.compile("id/(\\d+)");
    private static final Pattern PATTERN_INCISO =
            Pattern.compile("-\\s*(.*?)\\s*(?:\\||$)");

    @Named("limpiarHTML")
    public String limpiarHTML(String texto) {
        if (texto == null) return "";

        return texto
                .replaceAll("<br\\s*/?>", "\n")
                .replaceAll("<.*?>", "")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&sol;", "/")
                .replaceAll("&apos;", "'")
                .replaceAll("&quot;", "\"")
                .trim();
    }

    public Optional<Integer> extraerIdDelLink(String link) {
        if (link == null) return Optional.empty();

        String linkLimpio = link.replaceAll("[\"{}]", "").trim();
        Matcher m = PATTERN_ID.matcher(linkLimpio);

        return m.find() ? Optional.of(Integer.parseInt(m.group(1))) : Optional.empty();
    }

    @Named("extraerTipoLicitacion")
    public String extraerTipoLicitacion(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }

        String limpio = title.replaceAll("[\"{}]", "").trim();
        int index = limpio.indexOf(" - ");

        return index == -1
                ? null
                : limpio.substring(0, index).trim();
    }

    public Optional<LocalDateTime> extraerFechaCierre(String descripcion) {
        if (descripcion == null) return Optional.empty();

        Matcher matcher = PATTERN_HASTA.matcher(descripcion);
        if (matcher.find()) {
            try {
                return Optional.of(LocalDateTime.parse(matcher.group(1), FORMATTER_CIERRE));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public String extraerNombreInciso(String title) {

        if (title == null) return null;

        Matcher matcher = PATTERN_INCISO.matcher(title);

        return matcher.find()
                ? matcher.group(1).trim()
                : null;
    }

    @Named("parsearFechaPublicacion")
    public LocalDate parsearFechaPublicacion(String fechaRaw) {
        if (fechaRaw == null || fechaRaw.isEmpty()) return null;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(
                    fechaRaw,
                    DateTimeFormatter.RFC_1123_DATE_TIME
            );
            return odt.toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    @Named("extraerId")
    public Integer extraerIdMapper(String link) {
        return extraerIdDelLink(link).orElse(null);
    }

    @Named("extraerFechaCierre")
    public LocalDateTime extraerFechaCierreMapper(String descripcion) {
        return extraerFechaCierre(descripcion).orElse(null);
    }

    public LocalDateTime toStartOfDay(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.atStartOfDay();
    }

    public LocalDateTime toEndOfDay(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.atTime(23, 59, 59, 999_999_999);
    }
}


