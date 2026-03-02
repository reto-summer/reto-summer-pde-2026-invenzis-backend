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

    public LicitacionEmailService(ILicitacionEmailRepository licitacionEmailRepository, ILicitacionService licitacionService, IEmailService emailService,
                                  EmailMapper emailMapper, LicitacionMapper licitacionMapper) {
        this.licitacionEmailRepository = licitacionEmailRepository;
        this.licitacionService = licitacionService;
        this.emailService = emailService;
        this.emailMapper = emailMapper;
        this.licitacionMapper = licitacionMapper;
    }

    @Override
    @Transactional
    public void savePendingEmails() {
        List<LicitacionDTO> licitacionesDTO = getLicitaciones();
        List<EmailDTO> emailsDTO = emailService.findAllActive();
        if (licitacionesDTO.isEmpty() || emailsDTO.isEmpty()) {
            return;
        }

        Map<Integer, Licitacion> licitacionMap = mapLicitaciones(licitacionesDTO);
        Map<String, Email> emailMap = mapEmails(emailsDTO);

        Set<LicitacionEmail> emailsToSave = createLicitacionEmailSet(licitacionMap, emailMap);
        licitacionEmailRepository.saveAll(emailsToSave);
    }

    @Override
    @Transactional
    public void saveSentEmails() {
        List<LicitacionDTO> licitacionesDTO = getLicitaciones();
        List<EmailDTO> emailsDTO = emailService.findAllActive();
        if (licitacionesDTO.isEmpty() || emailsDTO.isEmpty()) return;

        Map<Integer, Licitacion> licitacionMap = mapLicitaciones(licitacionesDTO);
        Set<String> emailAddresses = emailsDTO.stream()
                .map(EmailDTO::getDireccionEmail)
                .collect(Collectors.toSet());

        List<Integer> licitacionIds = new ArrayList<>(licitacionMap.keySet());
        licitacionEmailRepository.updateEnviado(licitacionIds, emailAddresses);
    }

    private List<LicitacionDTO> getLicitaciones() {
        return licitacionService.findByFilters(null, null, LocalDate.now(), null, null, null);
    }

    private Map<Integer, Licitacion> mapLicitaciones(List<LicitacionDTO> licitacionesDTO) {
        return licitacionesDTO.stream()
                .collect(Collectors.toMap(LicitacionDTO::getIdLicitacion, licitacionMapper::licitacionDTOtoLicitacion));
    }

    private Map<String, Email> mapEmails(List<EmailDTO> emailsDTO) {
        return emailsDTO.stream()
                .collect(Collectors.toMap(EmailDTO::getDireccionEmail, emailMapper::emailDTOtoEmail));
    }

    private Set<LicitacionEmail> createLicitacionEmailSet(Map<Integer, Licitacion> licitacionMap, Map<String, Email> emailMap) {
        Set<LicitacionEmail> emailsToSave = new HashSet<>();
        for (Licitacion licitacion : licitacionMap.values()) {
            for (Email email : emailMap.values()) {
                LicitacionEmail licitacionEmail = new LicitacionEmail(licitacion, email);
                emailsToSave.add(licitacionEmail);
            }
        }
        return emailsToSave;
    }
}




