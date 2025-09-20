package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.services.SeguimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @PostMapping("/registrarSeguimiento")
    public SeguimientoDTO registrarSeguimiento(@RequestBody SeguimientoDTO seguimientoDTO) {
        return seguimientoService.registrar(seguimientoDTO);
    }

    @GetMapping("/listarSeguimientos")
    public List<SeguimientoDTO> listarPorDia(
            @RequestParam Integer pacienteId,
            @RequestParam LocalDate fecha) {
        return seguimientoService.listarPorDia(pacienteId, fecha);
    }

    @PutMapping("/editarSeguimiento/{seguimientoId}")
    public SeguimientoDTO editarRequerimientos(
            @PathVariable Integer seguimientoId,
            @RequestBody NutricionistaxRequerimientoDTO requerimientoNutriDTO) {
        return seguimientoService.editarRequerimientos(seguimientoId, requerimientoNutriDTO);
    }
}

