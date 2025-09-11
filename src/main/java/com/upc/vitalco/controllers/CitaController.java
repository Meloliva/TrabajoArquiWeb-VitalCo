package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.CitaDTO;
import com.upc.vitalco.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CitaController {
    @Autowired
    private CitaService citaService;

    @PostMapping("/registrarCita")
    public CitaDTO registrar(@RequestBody CitaDTO citaDTO){
        return citaService.registrar(citaDTO);
    }

    @GetMapping("/listarCitas")
    public List<CitaDTO> findAll(){
        return citaService.findAll();
    }
}
