package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ConfigService implements IConfigService
{
    private final IConfigRepository configRepository;
    private final ArceClientService arceClientService;
    private final IFamiliaService familiaService;
    private final ISubfamiliaService subfamiliaService;

    public ConfigService(IConfigRepository configRepository, ArceClientService arceClientService, IFamiliaService familiaService, ISubfamiliaService subfamiliaService)
    {
        this.configRepository = configRepository;
        this.arceClientService = arceClientService;
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
        return this.configRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("No hay Configuracion"));
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
            throw new IllegalArgumentException("Codigos de familia/subfamilia faltantes o incorrectos");
        }

        config.setFamilia(familia);
        config.setSubfamilia(subfamilia);
        this.configRepository.save(config);
        ArceRssFilters filters = new ArceRssFilters(config.getFamilia().getCod(), config.getSubfamilia().getCod());
        this.arceClientService.obtenerLicitaciones(filters);
        return this.getConfig();
    }
}
