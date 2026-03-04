package com.example.reto_backend_febrero2026.email;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    EmailDTO emailToDTO(Email email);

    Email emailDTOtoEmail(EmailDTO emailDTO);
}
