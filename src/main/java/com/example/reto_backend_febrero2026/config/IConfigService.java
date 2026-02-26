package com.example.reto_backend_febrero2026.config;

public interface IConfigService
{
    Config getConfig();

    Config updateConfig(ConfigUpdateDTO configUpdate);
}
