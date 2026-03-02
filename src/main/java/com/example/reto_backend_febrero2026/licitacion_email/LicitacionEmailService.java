package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.channel.email.EmailDTO;
import com.example.reto_backend_febrero2026.channel.email.EmailMapper;
import com.example.reto_backend_febrero2026.channel.email.IEmailService;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicitacionEmailService implements ILicitacionEmailService {

    private final ILicitacionEmailRepository licitacionEmailRepository;
    private final ILicitacionService licitacionService;
    private final IEmailService emailService;
    private final EmailMapper emailMapper;
    private final LicitacionMapper licitacionMapper;

    public LicitacionEmailService(ILicitacionEmailRepository licitacionEmailRepository, ILicitacionService licitacionService,
                                  IEmailService emailService, EmailMapper emailMapper, LicitacionMapper licitacionMapper) {
        this.licitacionEmailRepository = licitacionEmailRepository;
        this.licitacionService = licitacionService;
        this.emailService = emailService;
        this.emailMapper = emailMapper;
        this.licitacionMapper = licitacionMapper;
    }


    @Transactional
    public void savePendingEmails() {
        List<LicitacionDTO> licitacionesDTO = getLicitaciones();
        List<EmailDTO> emailsDTO = emailService.findAllActive();
        if (licitacionesDTO.isEmpty() || emailsDTO.isEmpty()) return;

        Set<String> existingKeys = licitacionEmailRepository.findByLicitacionesAndEmails(getLicitacionIds(licitacionesDTO), getEmailAddresses(emailsDTO));

        List<LicitacionEmail> toSave = new ArrayList<>();

        for (LicitacionDTO lDto : licitacionesDTO) {
            for (EmailDTO eDto : emailsDTO) {
                String compositeKey = lDto.getIdLicitacion() + "_" + eDto.getDireccionEmail();

                // Solo agrega si no existe el registro pendiente/enviado
                if (!existingKeys.contains(compositeKey)) {
                    Licitacion l = licitacionMapper.licitacionDTOtoLicitacion(lDto);
                    Email e = emailMapper.emailDTOtoEmail(eDto);
                    toSave.add(new LicitacionEmail(l, e));
                }
            }
        }

        if (!toSave.isEmpty()) {
            licitacionEmailRepository.saveAll(toSave);
            licitacionEmailRepository.flush();
        }
    }

    @Transactional
    public void saveSentEmails(List<Integer> licitacionIds, List<String> emailAddresses) {
        if (licitacionIds.isEmpty() || emailAddresses.isEmpty()) return;

        HashSet<String> direcciones = new HashSet<>(emailAddresses);

        licitacionEmailRepository.updateEnviado(licitacionIds, direcciones);
    }

    private List<LicitacionDTO> getLicitaciones() {
        return licitacionService.findByFilters(null, null, LocalDate.now(), null, null, null);
    }


    public List<LicitacionEmail> getPendientes() {
        return licitacionEmailRepository.findByEnviadoFalse();
    }

    private Set<String> getEmailAddresses(List<EmailDTO> emailsDTO) {
        return emailsDTO.stream()
                .map(EmailDTO::getDireccionEmail)
                .collect(Collectors.toSet());
    }

    private List<Integer> getLicitacionIds(List<LicitacionDTO> licitacionesDTO) {
        return licitacionesDTO.stream()
                .map(LicitacionDTO::getIdLicitacion)
                .collect(Collectors.toList());
    }
}


