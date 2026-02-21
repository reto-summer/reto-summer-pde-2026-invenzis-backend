package com.example.reto_backend_febrero2026.licitacion.service.implementation;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import com.example.reto_backend_febrero2026.licitacion.mapper.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LicitacionService implements ILicitacionService {

    private final ILicitacionRepository tenderRepository;
    private final LicitacionMapper tenderMapper;

    public LicitacionService(ILicitacionRepository tenderRepository,
                             LicitacionMapper tenderMapper) {
        this.tenderRepository = tenderRepository;
        this.tenderMapper = tenderMapper;
    }

    @Override
    public LicitacionModelDTO getTenderById(int id) {
        LicitacionModel tender = tenderRepository.getTenderById(id);
        return tenderMapper.tenderToTenderDTO(tender);
    }

    @Override
    public LicitacionModelDTO saveTender(LicitacionModelDTO dto) {

        LicitacionModel tender = tenderMapper.tenderDTOtoTender(dto);
        tenderRepository.save(tender);
        return tenderMapper.tenderToTenderDTO(tender);
    }

    /*@Override
    public LicitacionModelDTO getTenderByTitle(String titulo){
        LicitacionModel tender = tenderRepository.getTenderByTitle(titulo);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }
    @Override
    public LicitacionModelDTO getTenderByfecha_publicacion(LocalDate fecha){
        LicitacionModel tender = tenderRepository.getTenderByfecha_publicacion(fecha);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }
    @Override
    public LicitacionModelDTO getTenderByfecha_cierre(LocalDateTime fecha){
        LicitacionModel tender = tenderRepository.getTenderByfecha_cierre(fecha);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }

    @Override
    public LicitacionModelDTO getTenderByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin){
        LicitacionModel tender = tenderRepository.getTenderByfecha_inicio_fin(fecha_inicio, fecha_fin);
        return tenderMapper.licitacionToLicitacionDTO(tender);
    }
     */
}
