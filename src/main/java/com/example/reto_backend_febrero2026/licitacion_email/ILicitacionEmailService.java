package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;

import java.util.List;

public interface ILicitacionEmailService {

    void savePendingEmails();

    void saveSentEmails(List<Integer> licitacionIds, List<String> emailAddresses);

    List<LicitacionEmail> getPendientes();

    void save(Licitacion licitacion, Email email);
}
