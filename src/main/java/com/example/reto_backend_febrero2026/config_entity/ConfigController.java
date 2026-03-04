package com.example.reto_backend_febrero2026.config_entity;

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

    @GetMapping
    public ConfigDTO getConfig()
    {
        return this.configService.getConfig();
    }

    @PutMapping()
    public ConfigDTO updateConfig(@RequestBody ConfigUpdateDTO configUpdate)
    {
        return this.configService.updateConfig(configUpdate);
    }
}
