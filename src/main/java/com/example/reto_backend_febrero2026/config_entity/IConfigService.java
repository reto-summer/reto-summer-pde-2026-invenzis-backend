package com.example.reto_backend_febrero2026.config_entity;

public interface IConfigService
{
    ConfigDTO getConfig();

    Config getEntityConfig();

    ConfigDTO updateConfig(ConfigUpdateDTO configUpdate);
}
