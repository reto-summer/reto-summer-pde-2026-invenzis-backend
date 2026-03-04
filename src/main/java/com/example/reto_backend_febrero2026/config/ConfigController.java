package com.example.reto_backend_febrero2026.config;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Obtener configuración", description = "Devuelve la configuración actual del scheduler: familia y subfamilia para filtrado RSS.")
    @GetMapping
    public ConfigDTO getConfig()
    {
        return this.configService.getConfig();
    }

    @Operation(summary = "Actualizar configuración", description = "Actualiza familia y subfamilia del scheduler. Body: { familiaCod, subfamiliaCod }")
    @PutMapping()
    public ConfigDTO updateConfig(@RequestBody ConfigUpdateDTO configUpdate)
    {
        return this.configService.updateConfig(configUpdate);
    }
}
