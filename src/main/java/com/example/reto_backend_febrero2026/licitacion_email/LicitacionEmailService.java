package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.email.Email;
import com.example.reto_backend_febrero2026.email.IEmailRepository;
import com.example.reto_backend_febrero2026.licitacion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void registrarEnvios(List<LicitacionDTO> licitaciones, List<String> emails) {
        for (LicitacionDTO dto : licitaciones) {
            Licitacion licitacion = licitacionRepository.getReferenceById(dto.getIdLicitacion());
            for (String emailAddress : emails) {
                Email email = emailRepository.getReferenceById(emailAddress);
                LicitacionEmail registro = new LicitacionEmail(licitacion, email);
                licitacionEmailRepository.save(registro);
            }
        }
    }
}
