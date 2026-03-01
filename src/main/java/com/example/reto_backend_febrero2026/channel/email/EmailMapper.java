package com.example.reto_backend_febrero2026.channel.email;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    EmailDTO emailToDTO(Email email);

    Email emailDTOtoEmail(EmailDTO emailDTO);
}
