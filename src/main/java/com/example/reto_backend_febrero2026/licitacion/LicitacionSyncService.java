package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config_entity.Config;
import com.example.reto_backend_febrero2026.config_entity.IConfigService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicitacionSyncService implements ILicitacionSyncService{

    private final IConfigService configService;
    private final ArceClientService arceClientService;
    private final ILicitacionService licitacionService;
    private final LicitacionMapper licitacionMapper;

    public LicitacionSyncService(
            IConfigService configService,
            ArceClientService arceClientService,
            ILicitacionService licitacionService,
            LicitacionMapper licitacionMapper) {
        this.configService = configService;
        this.arceClientService = arceClientService;
        this.licitacionService = licitacionService;
        this.licitacionMapper = licitacionMapper;
    }

    @Override
    public void getLicitacionesByConfig() {
        try {
            Config config = configService.getEntityConfig();
            ArceRssFilters filters = new ArceRssFilters(
                    config.getFamilia().getCod(),
                    config.getSubfamilia().getCod()
            );

            List<LicitacionDTO> nuevasLicitaciones = arceClientService.obtenerLicitaciones(filters).get();

            for (LicitacionDTO dto : nuevasLicitaciones) {
                LicitacionItemRecord record = licitacionMapper.DTOtoLicitacionItemRecordItem(dto);
                licitacionService.save(record);
            }

            System.out.println(nuevasLicitaciones.size() + " licitaciones sincronizadas.");

        } catch (Exception e) {
            System.err.println("Error al sincronizar licitaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
