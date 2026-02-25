package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import org.springframework.stereotype.Service;

@Service
public class ConfigService implements IConfigService
{
    private final IConfigRepository configRepository;
    private final IFamiliaService familiaService;
    private final ISubfamiliaService subfamiliaService;

    public ConfigService(IConfigRepository configRepository, IFamiliaService familiaService, ISubfamiliaService subfamiliaService)
    {
        this.configRepository = configRepository;
        this.familiaService = familiaService;
        this.subfamiliaService = subfamiliaService;
    }

    public Config getConfig()
    {
        return this.configRepository.findById(1).orElseThrow(() -> new RuntimeException("NO HAY CONFIG"));
    }

    public Config updateConfig(ConfigUpdateDTO configUpdate)
    {
        Config config = this.getConfig();
        Familia familia;
        Subfamilia subfamilia;

        try
        {
            familia = familiaService.getEntityById(configUpdate.getFamiliaCod());
            subfamilia = subfamiliaService.getEntityById(configUpdate.getFamiliaCod(), configUpdate.getSubfamiliaCod());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Codigos de familia/subfamilia incorrectos");
        }

        config.setFamilia(familia);
        config.setSubfamilia(subfamilia);
        return this.configRepository.save(config);
    }
}
