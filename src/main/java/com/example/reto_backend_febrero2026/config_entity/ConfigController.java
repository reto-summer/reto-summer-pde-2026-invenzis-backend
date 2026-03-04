package com.example.reto_backend_febrero2026.config_entity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/config")
@CrossOrigin(origins = "*")
public class ConfigController
{
    private final IConfigService configService;

    public ConfigController(IConfigService configService)
    {
        this.configService = configService;
    }

    @Operation(summary = "Obtener configuración", description = "Devuelve la configuración actual (familia, subfamilia del scheduler).")
    @GetMapping
    public ConfigDTO getConfig()
    {
        return this.configService.getConfig();
    }

    @Operation(summary = "Actualizar configuración", description = "Actualiza familia y subfamilia del scheduler.")
    @PutMapping()
    public ConfigDTO updateConfig(@RequestBody ConfigUpdateDTO configUpdate)
    {
        return this.configService.updateConfig(configUpdate);
    }
}