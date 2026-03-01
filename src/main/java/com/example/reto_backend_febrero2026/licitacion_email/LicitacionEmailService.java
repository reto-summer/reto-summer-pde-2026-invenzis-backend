package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.channel.email.IEmailRepository;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LicitacionEmailService implements ILicitacionEmailService {

    private final ILicitacionEmailRepository licitacionEmailRepository;
    private final ILicitacionRepository licitacionRepository;
    private final IEmailRepository emailRepository;

    public LicitacionEmailService(ILicitacionEmailRepository licitacionEmailRepository,
                                  ILicitacionRepository licitacionRepository,
                                  IEmailRepository emailRepository) {
        this.licitacionEmailRepository = licitacionEmailRepository;
        this.licitacionRepository = licitacionRepository;
        this.emailRepository = emailRepository;
    }

    @Override
    @Transactional
    public void registrarPendientes(List<LicitacionDTO> licitaciones, List<String> emails) {
        List<Integer> licitacionIds = licitaciones.stream()
                .map(LicitacionDTO::getIdLicitacion)
                .collect(Collectors.toList());

        Set<LicitacionEmailId> existentes = licitacionEmailRepository
                .findByIdIdLicitacionIn(licitacionIds)
                .stream()
                .map(LicitacionEmail::getId)
                .collect(Collectors.toSet());

        for (LicitacionDTO dto : licitaciones) {
            Licitacion licitacion = licitacionRepository.getReferenceById(dto.getIdLicitacion());
            for (String emailAddress : emails) {
                LicitacionEmailId id = new LicitacionEmailId(dto.getIdLicitacion(), emailAddress);
                if (existentes.contains(id)) {
                    continue;
                }
                Email email = emailRepository.getReferenceById(emailAddress);
                licitacionEmailRepository.save(new LicitacionEmail(licitacion, email, false));
            }
        }
    }

    @Override
    @Transactional
    public void registrarEnvios(List<LicitacionDTO> licitaciones, List<String> emails) {
        for (LicitacionDTO dto : licitaciones) {
            Licitacion licitacion = licitacionRepository.getReferenceById(dto.getIdLicitacion());
            for (String emailAddress : emails) {
                LicitacionEmailId id = new LicitacionEmailId(dto.getIdLicitacion(), emailAddress);
                LicitacionEmail registro = licitacionEmailRepository.findById(id).orElseGet(() -> {
                    Email email = emailRepository.getReferenceById(emailAddress);
                    return new LicitacionEmail(licitacion, email, false);
                });

                registro.setEnviado(true);
                registro.setFechaEnvio(LocalDateTime.now());
                licitacionEmailRepository.save(registro);
            }
        }
    }
}
