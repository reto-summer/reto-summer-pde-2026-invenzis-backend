package com.example.reto_backend_febrero2026.inciso;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inciso")
@CrossOrigin(origins = "*")
public class IncisoController {

    private final IncisoService incisoService;

    public IncisoController(IncisoService incisoService){
        this.incisoService = incisoService;
    }

    @GetMapping
    public List<IncisoDTO> getAll(@RequestParam(required = false) String nombre) {
        if (nombre != null){
            return this.incisoService.getByNombre(nombre);
        }
        return this.incisoService.getAll();
    }

    @GetMapping("/{id}")
    public IncisoDTO getById(@PathVariable Integer id){
        return this.incisoService.getById(id);
    }

}
