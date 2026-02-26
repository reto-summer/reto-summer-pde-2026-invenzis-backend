package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
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

    public ConfigDTO getConfig()
    {
        Config config = this.getEntityConfig();
        SubfamiliaDTO subfamiliaDTO = new SubfamiliaDTO(config.getFamilia().getCod(), config.getSubfamilia().getCod(), config.getSubfamilia().getDescripcion());
        FamiliaDTO familiaDTO = new FamiliaDTO(config.getFamilia().getCod(), config.getFamilia().getDescripcion());
        return new ConfigDTO(config.getId(), familiaDTO, subfamiliaDTO);
    }

    public Config getEntityConfig()
    {
        return this.configRepository.findById(1).orElseThrow(() -> new RuntimeException("NO HAY CONFIG"));
    }

    public ConfigDTO updateConfig(ConfigUpdateDTO configUpdate)
    {
        Config config = this.getEntityConfig();
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
        this.configRepository.save(config);
        return this.getConfig();
    }
}
