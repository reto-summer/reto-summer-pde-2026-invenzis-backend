package com.example.reto_backend_febrero2026.subfamilia;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubfamiliaService implements ISubfamiliaService {

    private final SubfamiliaRepository subfamiliaRepository;

    public SubfamiliaService(SubfamiliaRepository subfamiliaRepository) {
        this.subfamiliaRepository = subfamiliaRepository;
    }

    @Override
    public List<SubfamiliaModel> findAll() {
        return subfamiliaRepository.findAll();
    }

    @Override
    public SubfamiliaModel findById(Integer famiCod, Integer cod) {

        SubfamiliaModel.SubfamiliaId id =
                new SubfamiliaModel.SubfamiliaId(famiCod, cod);

        return subfamiliaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Subfamilia no encontrada con famiCod: "
                                + famiCod + " y cod: " + cod
                ));
    }

    @Override
    public List<SubfamiliaModel> findByFamiCod(Integer famiCod) {
        return subfamiliaRepository.findByFamiCod(famiCod);
    }
}