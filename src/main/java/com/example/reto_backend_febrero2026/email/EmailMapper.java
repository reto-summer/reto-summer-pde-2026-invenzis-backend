package com.example.reto_backend_febrero2026.email;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    @Mapping(source = "emailAddress", target = "email")
    EmailDTO emailToEmailDTO(Email email);

    @Mapping(source = "email", target = "emailAddress")
    Email emailDTOtoEmail(EmailDTO emailDTO);
}
